class Piece{
    x: number;
    y: number;
    color: string;
    isQueen: boolean;
    constructor(x: number, y: number, color: string, isQueen: boolean){
        this.x = x;
        this.y = y;
        this.color = color;
        this.isQueen = isQueen;
    }
}

class Board{
    pieces: Array<Piece>;
    width: number;
    height: number;

    constructor(pieces: Array<Piece>, width: number, height: number){
        this.pieces = pieces;
        this.width = width;
        this.height = height;
    }

    getPiece(x: number, y: number) : Piece | null{
        return this.pieces.find(
            (value: Piece) => value.x === x && value.y === y
        ) || null;
    }
    removePiece(x: number, y: number) : void{
        this.pieces = this.pieces.filter(
            (value: Piece) => value.x !== x || value.y !== y
        );
    }
    addPiece(piece: Piece) : void{
        this.pieces.push(piece);
    }
}

class Move{
    start : Piece;
    end : Piece;

    isJump : boolean;
    jumpedPieces : Array<Piece>;

    constructor(start: Piece, end: Piece, isJump: boolean = false, jumpedPieces: Array<Piece> = []){
        this.start = start;
        this.end = end;
        this.isJump = isJump;
        this.jumpedPieces = jumpedPieces;
    }
}

namespace connection{
    export let serverUrl = "ws://localhost:8080";
    let socket : WebSocket;

    export function ConnectToServer(){ // TODO
        socket = new WebSocket(serverUrl);

        socket.onopen = (event: Event) => { // TODO
            console.log("Connected to server");
            html.log("Connected to server");

            SendRequest(JsonParser.SetVariant(html.settings.get("variant")));
            SendRequest(JsonParser.SetColor(html.settings.get("color")));
        }

        socket.onmessage = event => {   // TODO
            const json = JSON.parse(event.data);
            console.log("Received: ",json);

            const requestType = json.RequestType;
            const content = json.Content;

            switch(requestType){
                case "Init":
                    game.start(
                        content.Variant,
                        content.Color
                    );
                    SendRequest(
                        JsonParser.Request.GetBoard
                    );
                    break;
                case "Variant":
                    SendRequest(JsonParser.SetVariant(html.settings.get("variant")));
                    break;
                case "Color":
                    SendRequest(JsonParser.SetColor(html.settings.get("color")));
                    break;
                case "GetBoard":
                    const board = JsonParser.getBoard(content);
                    game.updateBoard(board);
                    break;
                case "GetState":
                    const state = content.State;
                    game.updateState(state);
                    break;
                case "Exception":
                    html.log(content.Message);
                    console.log(content.StackTrace);
                    SendRequest(
                        JsonParser.Request.GetBoard
                    );
                    SendRequest(
                        JsonParser.Request.GetState
                    )
                    break;
                default:
                    console.log("Unknown request type: ",requestType);
                    break;
            }
        }

        socket.onerror = event => { // TODO
            console.log("Error: " + event);
        }

        socket.onclose = event => { // TODO
            console.log("Lost connection to server");
            html.log("Lost connection to server");
        }
    }

    export function SendRequest(request: string){   // TODO
        console.log("Sent: " + request);
        socket.send(request);
    }

    export namespace JsonParser {
        export function SetVariant(variant: string) : string{ // TODO: check if it works
            const request : any = {
                RequestType: "SetVariant",
                Content: {
                    Variant: variant
                }
            }
            return JSON.stringify(request);
        }
        export function SetColor(color: string) : string{
            const request : any = {
                RequestType: "SetColor",
                Content: {
                    Color: color
                }
            }
            return JSON.stringify(request);
        }

        export function getBoard(json) : Board{ // TODO: check if it works
            let pieces = json.Pieces.map(
                (value: any) => {
                    return new Piece(value.x,value.y,value.Color,value.isQueen);
                }
            );
            return new Board(pieces,10,10); // TODO: get width and height from json
        }

        export function getMove(json: string) : Move{ // TODO: check if it works
            let obj = JSON.parse(json);
            let move : Move = new Move(
                new Piece(obj.Start.x,obj.Start.y,obj.Start.color,obj.Start.isQueen),
                new Piece(obj.End.x,obj.End.y,obj.End.color,obj.End.isQueen),
                obj.IsJump,
                obj.JumpedPieces.map(
                    (value: any) => {
                        return new Piece(value.x,value.y,value.color,value.isQueen);
                    }
                )
            );

            return move;
        }

        export function boardJson(board: Board) : string{ // TODO: check if it works
            let boardJson = {
                Width: board.width,
                Height: board.height,
                Pieces: board.pieces.map(
                    (value: Piece) => {
                        return {
                            x: value.x,
                            y: value.y,
                            color: value.color,
                            isQueen: value.isQueen
                        }
                    }
                )
            };

            let obj = {
                RequestType: "Board",
                Board: boardJson
            };
            return JSON.stringify(obj);
        }

        export function moveJson(move: Move) : string{ // TODO: check if it works
            let moveJson = {
                Start: {
                    x: move.start.x,
                    y: move.start.y,
                    color: move.start.color,
                    isQueen: move.start.isQueen
                },
                End: {
                    x: move.end.x,
                    y: move.end.y,
                    color: move.end.color,
                    isQueen: move.end.isQueen
                },
                IsJump: move.isJump,
                JumpedPieces: move.jumpedPieces.map(
                    (value: Piece) => {
                        return {
                            x: value.x,
                            y: value.y,
                            color: value.color,
                            isQueen: value.isQueen
                        }
                    }
                )
            };
            let obj = {
                RequestType: "Move",
                Content: moveJson
            };

            return JSON.stringify(obj);
        }

        export const enum Request {
            GetColor = '{"RequestType":"GetColor","Content":{}}',
            GetBoard = '{"RequestType":"GetBoard","Content":{}}',
            GetState = '{"RequestType":"GetState","Content":{}}'
        }
    }


}

namespace html{
    export let settings : Map<string,string> = new Map<string,string>();

    export function startWindow(){  // Create HTML div with id="startWindow" inside div with id="content"
        const div = document.getElementById('startWindow') as HTMLDivElement;
        const textField = document.getElementById('address') as HTMLInputElement;
        const variantSelector = document.getElementById('variant') as HTMLSelectElement;
        const colorSelector = document.getElementById('color') as HTMLSelectElement;
        const button = document.getElementById('connect') as HTMLButtonElement;
        const log = document.getElementById('log') as HTMLDivElement;
        const hidelog = document.getElementById('hide-log') as HTMLButtonElement;
        
        textField.setAttribute('value',connection.serverUrl);
        game.variants.forEach(
            value => {
                const option = document.createElement('option');
                option.setAttribute('value',value);
                option.innerText = value;
                variantSelector.appendChild(option);
            }
        );
        game.variantsColors.get(game.variants[0]).forEach(
            value => {
                const option = document.createElement('option');
                option.setAttribute('value',value);
                option.innerText = value;
                colorSelector.appendChild(option);
            }
        );
        variantSelector.onchange = ()=>{
            colorSelector.innerHTML = "";
            game.variantsColors.get(variantSelector.value).forEach(
                value => {
                    const option = document.createElement('option');
                    option.setAttribute('value',value);
                    option.innerText = value;
                    colorSelector.appendChild(option);
                }
            );
        }
        button.onclick = ()=>{
            settings.set('variant',variantSelector.value);
            settings.set('color',colorSelector.value);
            connection.serverUrl = textField.value;
            connection.ConnectToServer();
        };

        hidelog?.addEventListener('click',()=>{
            log?.classList.toggle('hidden');
            hidelog?.classList.toggle('hidden');
        })
    }

    export function removeStartWindow(){  // Remove div with id="startWindow"
        const div = document.getElementById('startWindow');
        if(div !== null){
            div.remove();
        }
    }

    export function createBoard(width : number, height : number){   // Create HTML table with id="board" inside div with id="content"
        const table = document.createElement('table');
        table.setAttribute('id','board');
    
        for(let y = 0; y < height; y++){
            const tr = table.insertRow();   // Create row
            for(let x = 0; x < width; x++){
                const ce = tr.insertCell(); // Create cell
                ce.setAttribute('color',((x+y)%2 === 0)?'black':'white');   // Set color to black or white
                ce.setAttribute('piece','NONE');    // Set piece to NONE (we will use this later)
                ce.setAttribute('state','neutral'); // Set state to neutral (we will use this later)
                ce.onclick = ()=>{ // Set onclick event
                    tdClicked(x,y);
                };
    
                ce.setAttribute('x',x.toString()); // Set x and y attributes
                ce.setAttribute('y',y.toString());
            }
        }
    
        document.getElementById("content")?.appendChild(table); // Append table to div with id="content"
    }

    function tdClicked(x: number, y: number){
        game.click(x,y);    // Call game.click(x,y) that will handle game logic
    }

    export function updateBoard(board: Board){  // Update HTML table to match board
        const table = <HTMLTableElement>document.getElementById('board');
        if(table === null){
            return;
        }

        for(let y = 0; y < board.height; y++){
            const tr = table.rows[y];
            for(let x = 0; x < board.width; x++){
                const ce = tr.cells[x];

                resetCell(ce);
                const piece = board.getPiece(x,y);
                if(piece !== null){
                    ce.setAttribute('piece',piece.color);
                    ce.setAttribute('isQueen',piece.isQueen.toString());
                }
            }
        }
    }

    function resetCell(cell: HTMLTableCellElement){ // Reset cell to default values
        cell.setAttribute('piece','NONE');
        cell.setAttribute('state','neutral');
    }

    export function setCellState(x: number, y: number, state: string){  // Set state of cell
        const table = <HTMLTableElement>document.getElementById('board');
        if(table === null){
            return;
        }

        const tr = table.rows[y];
        const ce = tr.cells[x];
        ce.setAttribute('state',state);
    }

    export function log(text: string, properties: string = ""){ // Log text to div with id="log"
        const log = document.getElementById('log');
        if(log === null){
            return;
        }

        const p = document.createElement('p');
        p.innerText = text;
        if(properties !== "")
            p.setAttribute('style',properties);
        log.prepend(p);
    }
}

namespace game{
    let PlayerColor : string = "white";
    let Turn : string = "white";
    let Variant : string = "Standard";

    let board : Board;

    let selectedPiece : [number,number] | null = null;
    let availableMoves : Map<String,[number,number][]> = new Map();

    let move : Move | null = null;

    export const variants : string[] = [
        "Standard","20x20"
    ];
    export const variantsColors : Map<string, string[]> = new Map([
        ["Standard",["white","black"]],
        ["20x20", ["white","black"]]
    ]);
    export const variantSizes : Map<string, [number,number]> = new Map([
        ["Standard",[10,10]],
        ["20x20", [20,20]]
    ]);

    export function init(){
        html.startWindow();
    }

    export function start(variant: string, color: string){
        Variant = variant;
        PlayerColor = color;

        html.removeStartWindow();

        html.log("Starting game with variant: " + variant + " and color: " + color);
        console.log("Starting game with variant: " + variant + " and color: " + color);

        html.createBoard(
            variantSizes.get(variant)[0],
            variantSizes.get(variant)[1]
        );
    }

    export function updateBoard(newBoard: Board){
        board = newBoard;
        html.updateBoard(board);

        availableMoves.clear();
        jumpAvailable = false;
        board.pieces.forEach(
            val => {
                if(val.color === PlayerColor){
                    availableMoves.set([val.x,val.y].toString(),getAvailableJumps.get(Variant)(val.x,val.y));
                }
            }
        );
        board.pieces.forEach(
            val => {
                if(val.color === PlayerColor){
                    availableMoves.get([val.x,val.y].toString()).push(...getAvailableMoves.get(Variant)(val.x,val.y));
                }
            }
        );
    }

    export function updateState(state: string){
        Turn = state;
        html.log("Turn: " + state);
        console.log("Turn: " + state);
    }

    export function click(x: number, y: number){
        if(Turn !== PlayerColor){
            return;
        }

        if(board.getPiece(x,y)?.color === PlayerColor){
            deselectPiece();
            selectedPiece = [x,y];
            html.setCellState(x,y,"selected");

            availableMoves.get([x,y].toString()).forEach(
                val => html.setCellState(val[0],val[1],"available")
            );
        } else if(selectedPiece !== null && availableMoves.get(selectedPiece.toString()).some(val => val[0] === x && val[1] === y)){
            makeMove(x,y);
            deselectPiece();
            updateBoard(board);

            if(!jumpAvailable){
                console.log("Changing turn");
                Turn = Turn === "white" ? "black" : "white";
                connection.SendRequest(
                    connection.JsonParser.moveJson(move)
                );
                move = null;
            }
        } else {
            deselectPiece();
        }
    }

    function makeMove(x : number, y : number){
        if(Turn !== PlayerColor)
            return;
        const px = selectedPiece[0];
        const py = selectedPiece[1];

        const dx = x - px;
        const dy = y - py;

        const isJump : boolean = Math.abs(dx) === 2 && Math.abs(dy) === 2;
        const isQueen : boolean = board.getPiece(selectedPiece[0],selectedPiece[1]).isQueen?true:
            (PlayerColor === "white" && y === board.height - 1) || (PlayerColor === "black" && y === 0);

        if(move === null){
            move = new Move(
                new Piece(px,py,PlayerColor,isQueen),
                new Piece(x,y,PlayerColor,isQueen),
                isJump,
                []
            );

            if(isJump){
                const mx = selectedPiece[0] + dx/2;
                const my = selectedPiece[1] + dy/2;
                const jumped = board.getPiece(mx,my);
                move.jumpedPieces.push(jumped);
            }
        } else {
            move.end.x = x;
            move.end.y = y;

            const mx = selectedPiece[0] + dx/2;
            const my = selectedPiece[1] + dy/2;
            const jumped = board.getPiece(mx,my);
            move.jumpedPieces.push(jumped);
        }

        board.removePiece(px,py);
        if(isJump)
            board.removePiece(px + dx/2,py + dy/2);
        board.addPiece(new Piece(x,y,PlayerColor,isQueen));
    }

    function deselectPiece(){
        if(selectedPiece === null)
            return;
        html.setCellState(selectedPiece[0],selectedPiece[1],"neutral");
        availableMoves.get(selectedPiece.toString()).forEach(
            val => html.setCellState(val[0],val[1],"neutral")
        );
        selectedPiece = null;
    }

    let jumpAvailable = false;

    const getAvailableJumps : Map<string, (x: number, y: number) => [number,number][]> = new Map([
        ["Standard", (px: number, py: number) => {
            let moves : [number,number][] = [];
            const enemyColor = PlayerColor === "white" ? "black" : "white";

            if(move !== null && (move.end.x !== px || move.end.y !== py || !move.isJump)){
                    return moves;
            }

            for(let x = px-1; x<=px+1; x+=2)
                for(let y = py-1; y<=py+1; y+=2)
                    if(board.getPiece(x,y)?.color === enemyColor){
                        let dx = x-px;  let dy = y-py;
                        if(board.getPiece(x+dx,y+dy) === null){
                            if(x+dx < 0 || x+dx >= board.width || y+dy < 0 || y+dy >= board.height)
                                continue;
                            jumpAvailable = true;
                            moves.push([x+dx,y+dy]);
                        }
                    }
            return moves;
        }]
    ]);

    const getAvailableMoves : Map<string, (x: number, y: number) => [number,number][]> = new Map([
        ["Standard", (px: number, py: number) => {
            if(jumpAvailable || move !== null)
                return [];

            let moves : [number,number][] = [];
            const dy = PlayerColor === "white" ? 1 : -1;

            for(let x = px-1; x<=px+1; x+=2)
                for(let y = py-1; y<=py+1; y+=2)
                    if(board.getPiece(x,y) === null && (y === py+dy || board.getPiece(px,py)?.isQueen)){
                        if(x >= board.width || x < 0 || y >= board.height || y < 0)
                            continue;
                        else
                            moves.push([x,y]);
                    }
            return moves;
        }]
    ]);
}

game.init();