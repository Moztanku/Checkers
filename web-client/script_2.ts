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

            const request_1 : any = {
                RequestType: "SetVariant",
                Content: {
                    Variant: html.settings.get("variant")
                }
            }
            const request_2 : any = {
                RequestType: "SetColor",
                Content: {
                    Color: html.settings.get("color")
                }
            }
            SendRequest(JSON.stringify(request_1));
            SendRequest(JSON.stringify(request_2));
        }

        socket.onmessage = event => {   // TODO
            const json = JSON.parse(event.data);
            console.log("Received: " + json);

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
                case "GetBoard":
                    const board = JsonParser.getBoard(content);
                    game.updateBoard(board);
                    break;
                default:
                    console.log("Unknown request type: " + requestType);
                    break;
            }
        }

        socket.onerror = event => { // TODO
            console.log("Error: " + event);
        }

        socket.onclose = event => { // TODO
            console.log("Connection closed");
        }
    }

    export function SendRequest(request: string){   // TODO
        socket.send(request);
    }

    export namespace JsonParser {
        export function getColor(json: string) : string{ // TODO: check if it works
            return JSON.parse(json).Color;
        }

        export function getBoard(json) : Board{ // TODO: check if it works
            console.log(json);

            let pieces = json.Pieces.map(
                (value: any) => {
                    return new Piece(value.x,value.y,value.Color,value.isQueen);
                }
            );
            console.log(pieces);
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
                Move: moveJson
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
        const div = document.createElement('div');

        div.setAttribute('id','startWindow');

        const header = document.createElement('h1');
        header.innerText = "Checkers";
        div.appendChild(header);

        // create label for server address
        const label_1 = document.createElement('label');
        label_1.setAttribute('for','address');
        label_1.innerText = "Server address:  ";
        div.appendChild(label_1);

        // create text field
        const textField = document.createElement('input');
        textField.setAttribute('type','text');
        textField.setAttribute('id','address');
        textField.setAttribute('name','address');
        textField.setAttribute('value',connection.serverUrl);
        div.appendChild(textField);

        div.appendChild(document.createElement('br'));
        // create label for select
        const label_2 = document.createElement('label');
        label_2.setAttribute('for','variant');
        label_2.innerText = "Game variant:  ";
        div.appendChild(label_2);
        // create select
        const select = document.createElement('select');
        select.setAttribute('id','variant');
        select.setAttribute('name','variant');
        // create options
        game.variants.forEach(
            value => {
                const option = document.createElement('option');
                option.setAttribute('value',value);
                option.innerText = value;
                select.appendChild(option);
            }
        );
        div.appendChild(select);
        
        div.appendChild(document.createElement('br'));
        // create select for color
        const label_3 = document.createElement('label');
        label_3.setAttribute('for','color');
        label_3.innerText = "Color:  ";
        div.appendChild(label_3);
        // create select
        const select_2 = document.createElement('select');
        select_2.setAttribute('id','color');
        select_2.setAttribute('name','color');
        // create options
        game.variantsColors.get(game.variants[0]).forEach(
            value => {
                const option = document.createElement('option');
                option.setAttribute('value',value);
                option.innerText = value;
                select_2.appendChild(option);
            }
        );
        select.onchange = ()=>{
            select_2.innerHTML = "";
            game.variantsColors.get(select.value).forEach(
                value => {
                    const option = document.createElement('option');
                    option.setAttribute('value',value);
                    option.innerText = value;
                    select_2.appendChild(option);
                }
            );
        }
        div.appendChild(select_2);
        div.appendChild(document.createElement('br'));
        // create button
        const button = document.createElement('button');
        button.innerText = "Connect";
        button.classList.add('button-4');
        button.onclick = ()=>{
            settings.set('variant',select.value);
            settings.set('color',select_2.value);

            connection.serverUrl = textField.value;
            connection.ConnectToServer();
        };
        div.appendChild(button);

        document.getElementById("content")?.appendChild(div);
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
        log("Updating board with pieces");
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

    export const variants : string[] = [
        "Standard"
    ];
    export const variantsColors : Map<string, string[]> = new Map([
        ["Standard",["white","black"]]
    ]);
    export const variantSizes : Map<string, [number,number]> = new Map([
        ["Standard",[10,10]]
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
        console.log(board);
        html.updateBoard(board);
    }

    export function click(x: number, y: number){
    }
}

game.init();