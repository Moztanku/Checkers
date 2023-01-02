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
}

function tdClicked(x: number, y: number){
    console.log(`td clicked at x:${x} y:${y}`);
}

function createHTMLBoard(height: number, width: number){
    const table = document.createElement('table');
    table.setAttribute('id','board');

    for(let y = 0; y < height; y++){
        const tr = table.insertRow();
        for(let x = 0; x < width; x++){
            const ce = tr.insertCell();
            ce.setAttribute('color',((x+y)%2 === 0)?'black':'white');
            ce.setAttribute('piece','NONE');
            ce.onclick = ()=>{
                tdClicked(x,y);
            };

            ce.setAttribute('x',x.toString());
            ce.setAttribute('y',y.toString());
        }
    }

    document.getElementById("content")?.appendChild(table);
}

function getCell(x: number, y: number){
    return document.querySelector(`td[x="${x}"][y="${y}"]`);
};

function drawPieces(pieces: Array<Piece>){
    pieces.forEach(
        (value: Piece) => {
            let cell = getCell(value.x,value.y);
            cell?.setAttribute('piece',value.color);
        }
    )
}

function parseBoardJson(boardJson: string): Board{
    let board: any = JSON.parse(boardJson);

    let pieces: Piece[] = [];
    board.Pieces.forEach(
        (value: any) => {
            pieces.push(
                new Piece(value.x, value.y, value.color, value.isQueen)
            )
        }
    );
    let width = board.Width;
    let height = board.Height;
    
    return new Board(pieces,width,height);
}

const boardJson = `{
    "Pieces":[
        {"x":0, "y":0, "color":"WHITE" ,"isQueen":false},
        {"x":2, "y":0, "color":"WHITE" ,"isQueen":false},
        {"x":0, "y":7, "color":"BLACK" ,"isQueen":false},
        {"x":2, "y":7, "color":"BLACK" ,"isQueen":false}
    ],
    "Width":8,
    "Height":8
}`;

let board = parseBoardJson(boardJson);

createHTMLBoard(8,8);
drawPieces(board.pieces);