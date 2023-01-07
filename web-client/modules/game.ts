namespace game{
    let PlayerColor : string = "white";
    let Turn : string = "white";

    let board : Board;

    let selectedPiece : [number,number] | null = null;
    let availableMoves : Map<String,[number,number][]> = new Map();

    let move : Move | null = null;

    export function start(variant: string, color: string){
        variants.setVariant(variant);
        variants.setPlayerColor(color);
        
        PlayerColor = color;

        html.removeStartWindow();

        html.log("Starting game with variant: " + variant + " and color: " + color);
        console.log("Starting game with variant: " + variant + " and color: " + color);

        html.createBoard(
            variants.getSize()[0],
            variants.getSize()[1]
        );
    }

    export function updateBoard(newBoard: Board){
        board = newBoard;
        html.updateBoard(board);

        availableMoves.clear();
        variants.setBoard(board);
        variants.setMove(move);
        board.pieces.forEach(
            val => {
                if(val.color === PlayerColor){
                    availableMoves.set([val.x,val.y].toString(),variants.getAvailableJumps(val.x,val.y));
                }
            }
        );
        board.pieces.forEach(
            val => {
                if(val.color === PlayerColor){
                    availableMoves.get([val.x,val.y].toString()).push(...variants.getAvailableMoves(val.x,val.y));
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

            if(!variants.getJumpAvailable()){
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
}