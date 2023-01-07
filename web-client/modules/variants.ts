namespace variants {
    export const Names : string[] = [
        "Polish", "English", "Italian"
    ];
    export const Colors : Map<string, string[]> = new Map<string, string[]>([
        ["Polish", ["white","black"]],
        ["English", ["white","black"]],
        ["Italian", ["white","black"]]
    ]);
    export const Sizes : Map<string, [number, number]> = new Map<string, [number, number]>([
        ["Polish", [10,10]],
        ["English", [8,8]],
        ["Italian", [8,8]]
    ]);
    const AvailableJumpsFunctions: Map<string, (x: number, y: number) => [number,number][]> = new Map([
        ["Polish", (px: number, py: number) => {
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
        }],
        ["English", (px: number, py: number) => {
            let moves : [number, number][] = [];

            if(move !== null && (move.end.x !== px || move.end.y !== py || !move.isJump)){
                return moves;
            }

            for(let x = px-1; x <= px+1; x+=2)
                for(let y = py-1; y <= py+1; y+=2){
                    if(board.getPiece(x,y) === null || board.getPiece(x,y).color === PlayerColor)
                        continue;
                    if(board.getPiece(px,py)?.isQueen || PlayerColor === "white" && y > py || PlayerColor === "black" && y < py){
                        let dx = x-px;  let dy = y-py;
                        if(board.getPiece(x+dx,y+dy) === null){
                            if(x+dx < 0 || x+dx >= board.width || y+dy < 0 || y+dy >= board.height)
                                continue;
                            jumpAvailable = true;
                            moves.push([x+dx,y+dy]);
                        }
                    }
                }

            return moves;
        }],
        ["Italian", (px: number, py: number) => {
            let moves : [number, number][] = [];

            return moves;
        }]
    ]);

    const AvailableMovesFunctions: Map<string, (x: number, y: number) => [number,number][]> = new Map([
        ["Polish", (px: number, py: number) => {
            let moves : [number,number][] = [];
            const dy = PlayerColor === "white" ? 1 : -1;

            if(jumpAvailable || move !== null)
                return moves;

            for(let x = px-1; x<=px+1; x+=2)
                for(let y = py-1; y<=py+1; y+=2)
                    if(board.getPiece(x,y) === null && (y === py+dy || board.getPiece(px,py)?.isQueen)){
                        if(x >= board.width || x < 0 || y >= board.height || y < 0)
                            continue;
                        else
                            moves.push([x,y]);
                    }
            return moves;
        }],
        ["English", (px: number, py: number) => {
            let moves : [number, number][] = [];

            if(jumpAvailable || move !== null)
                return moves;

            for(let x = px-1; x <= px+1; x+=2)
                for(let y = py-1; y <= py+1; y+=2){
                    if(board.getPiece(x,y) !== null || x >= board.width || x < 0 || y >= board.height || y < 0)
                        continue;
                    if(board.getPiece(px,py)?.isQueen || PlayerColor === "white" && y > py || PlayerColor === "black" && y < py){
                        moves.push([x,y]);
                    }
                }

            return moves;
        }],
        ["Italian", (px: number, py: number) => {
            let moves : [number, number][] = [];

            return moves;
        }]
    ]);

    let varriant : string = "Polish";
    export function setVariant(v: string){
        varriant = v;
    };

    let jumpAvailable = false;
    export  function getJumpAvailable() : boolean {
        return jumpAvailable;
    }

    let move : Move | null = null;
    export function setMove(m: Move | null){
        jumpAvailable = false;
        move = m;
    }

    let PlayerColor : string = "white";
    export function setPlayerColor(c: string){
        PlayerColor = c;
    }

    let board : Board | null = null;
    export function setBoard(b: Board | null){
        board = b;
    }

    export function getAvailableJumps(x: number, y: number) : [number,number][] {
        return AvailableJumpsFunctions.get(varriant)?.(x,y);
    }
    export function getAvailableMoves(x: number, y: number) : [number,number][] {
        return AvailableMovesFunctions.get(varriant)?.(x,y);
    }

    export function getColors() : string[] {
        return Colors.get(varriant);
    }
    export function getSize() : [number, number] {
        return Sizes.get(varriant);
    }
}