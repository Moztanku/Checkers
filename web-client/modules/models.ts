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

