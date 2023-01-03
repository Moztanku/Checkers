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


        }

        socket.onmessage = event => {   // TODO
            console.log(event.data);
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

        export function getBoard(json: string) : Board{ // TODO: check if it works
            let obj = JSON.parse(json);
            let pieces = obj.Pieces.map(
                (value: any) => {
                    return new Piece(value.x,value.y,value.color,value.isQueen);
                }
            );
            return new Board(obj.Width,obj.Height,pieces);
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
            GetColor = "{\"RequestType\":\"GetColor\"}",
            GetBoard = "{\"RequestType\":\"GetBoard\"}",
            GetState = "{\"RequestType\":\"GetState\"}"
        }
    }


}

namespace html{
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
        // create button
        const button = document.createElement('button');
        button.innerText = "Connect";
        button.classList.add('button-4');
        button.onclick = ()=>{
          connection.serverUrl = textField.value;
          /* TODO: connection stuff */
          connection.ConnectToServer();
        };
        div.appendChild(button);

        document.getElementById("content")?.appendChild(div);
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
    export let PlayerColor : string = "white";
    export let Turn : string = "white";

    export let board : Board;

    export const variants : string[] = [
        "Standard"
    ]

    export function init(){
        html.startWindow();
    }

    export function click(x: number, y: number){
    }
}

game.init();
