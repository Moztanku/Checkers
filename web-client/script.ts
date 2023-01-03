// class Piece{
//     x: number;
//     y: number;
//     color: string;
//     isQueen: boolean;
//     constructor(x: number, y: number, color: string, isQueen: boolean){
//         this.x = x;
//         this.y = y;
//         this.color = color;
//         this.isQueen = isQueen;
//     }
// }

// class Board{
//     pieces: Array<Piece>;
//     width: number;
//     height: number;

//     constructor(pieces: Array<Piece>, width: number, height: number){
//         this.pieces = pieces;
//         this.width = width;
//         this.height = height;
//     }

//     getPiece(x: number, y: number) : Piece | null{
//         return this.pieces.find(
//             (value: Piece) => value.x === x && value.y === y
//         ) || null;
//     }
//     removePiece(x: number, y: number) : void{
//         this.pieces = this.pieces.filter(
//             (value: Piece) => value.x !== x || value.y !== y
//         );
//     }
//     addPiece(piece: Piece) : void{
//         this.pieces.push(piece);
//     }
// }

// namespace connection{
//     export function parseBoardJson(boardJson: string): Board{
//         let board: any = JSON.parse(boardJson);
    
//         let pieces: Piece[] = [];
//         board.Pieces.forEach(
//             (value: any) => {
//                 pieces.push(
//                     new Piece(value.x, value.y, value.color, value.isQueen)
//                 )
//             }
//         );
//         let width = board.Width;
//         let height = board.Height;
        
//         return new Board(pieces,width,height);
//     }
// }

// namespace client{
//     export function createHTMLBoard(height: number, width: number){
//         const table = document.createElement('table');
//         table.setAttribute('id','board');
    
//         for(let y = 0; y < height; y++){
//             const tr = table.insertRow();
//             for(let x = 0; x < width; x++){
//                 const ce = tr.insertCell();
//                 ce.setAttribute('color',((x+y)%2 === 0)?'black':'white');
//                 ce.setAttribute('piece','NONE');
//                 ce.setAttribute('state','neutral');
//                 ce.onclick = ()=>{
//                     tdClicked(x,y);
//                 };
    
//                 ce.setAttribute('x',x.toString());
//                 ce.setAttribute('y',y.toString());
//             }
//         }
    
//         document.getElementById("content")?.appendChild(table);
//     }

//     let playerColor : string = 'WHITE';
//     let tdActive : [number,number] | null = null;
//     let availableMoves : Array<[number,number]> = [];
//     let movedPiece : Piece | null = null;

//     function getAvailableMoves(pos : [number,number]) : Array<[number,number]>{
//         let moves : Array<[number,number]> = [];

//         for(let x = pos[0]-1; x <= pos[0]+1; x+=2){
//             for(let y = pos[1]-1; y <= pos[1]+1; y+=2){
//                 const piece : Piece | null = board.getPiece(x,y);
//                 if(piece === null){ // If the adjacent cell is empty
//                     if(
//                         movedPiece === null && // If the piece has not moved yet
//                         ((playerColor === 'WHITE' && y > pos[1]) || // If the piece is white and the adjacent cell is below
//                         (playerColor === 'BLACK' && y < pos[1]) || // or if the piece is black and the adjacent cell is above
//                         board.getPiece(pos[0],pos[1]).isQueen === true) // or if the piece is a queen
//                     ){
//                         moves.push([x,y]);  // Add the adjacent cell to the available moves
//                     }
//                 } else if(
//                     piece.color === 'BLACK' &&  // If the adjacent cell is an enemy piece
//                     board.getPiece(
//                         pos[0]+2*(x-pos[0]),
//                         pos[1]+2*(y-pos[1])
//                     ) === null  // and the cell after the enemy piece is empty
//                 ){
//                     moves.push(
//                         [pos[0]+2*(x-pos[0]),pos[1]+2*(y-pos[1])]   // Add the cell after the enemy piece to the available moves
//                     );
//                 }
//             }
//         }
//         return moves;
//     }

//     function tdClicked(x: number, y: number){
//         const piece : Piece | null = board.getPiece(x,y);
//         if(tdActive === null){  // If no piece is active
//             if(piece?.color === playerColor){
//                 tdActive = [x,y];
//                 availableMoves = getAvailableMoves(tdActive);

//                 setStateTd(tdActive,'active');
//                 availableMoves.forEach(
//                     pos => setStateTd(pos,'available')
//                 );
//             }
//         } else if(movedPiece === null && tdActive[0] === x && tdActive[1] === y || !availableMoves.some(pair => pair[0] === x && pair[1] === y)){  // If the user pressed the active piece or a non available move
//             setStateTd(tdActive,'neutral');
//             availableMoves.forEach(
//                 pos => setStateTd(pos,'neutral')
//             );

//             tdActive = null;
//             availableMoves = [];

//             tdClicked(x,y); // Check if the user pressed another a piece of his color
//         } else{
//             setStateTd(tdActive,'neutral');
//             availableMoves.forEach(
//                 pos => setStateTd(pos,'neutral')
//             );

//             movedPiece = makeMove(x,y);

//             tdActive = [movedPiece.x,movedPiece.y];
//             availableMoves = getAvailableMoves(tdActive);

//             setStateTd(tdActive,'active');
//             availableMoves.forEach(
//                 pos => setStateTd(pos,'available')
//             );
//         }

//         console.log("Active move:",tdActive);
//         console.log("Available moves:",availableMoves);
//     }

//     function setStateTd(pos : [number,number], state : string){
//         let cell = getCell(pos[0],pos[1]);
//         cell?.setAttribute('state',state);
//     }

//     function makeMove(x: number, y: number) : Piece{
//         if(tdActive === null){
//             throw new Error('No active piece');
//         }

//         let start = board.getPiece(tdActive[0],tdActive[1]);
//         if(start === null){
//             throw new Error('No piece at active position');
//         }

//         let end = new Piece(x,y,start.color,start.isQueen);

//         board.removePiece(tdActive[0],tdActive[1]);
//         board.addPiece(end);

//         return end;
//     }

//     export function drawPieces(pieces: Array<Piece>){
//         pieces.forEach(
//             (value: Piece) => {
//                 let cell = getCell(value.x,value.y);
//                 cell?.setAttribute('piece',value.color);
//             }
//         )
//     }

//     function getCell(x: number, y: number){
//         return document.querySelector(`td[x="${x}"][y="${y}"]`);
//     };
// }

// const boardJson = `{
//     "Pieces":[
//         {"x":0, "y":0, "color":"WHITE", "isQueen":false},
//         {"x":2, "y":0, "color":"WHITE", "isQueen":false},
//         {"x":1, "y":1, "color":"WHITE", "isQueen":false},
//         {"x":4, "y":4, "color":"WHITE", "isQueen":false},
//         {"x":3, "y":3, "color":"BLACK", "isQueen":false},
//         {"x":3, "y":5, "color":"BLACK", "isQueen":false},
//         {"x":5, "y":3, "color":"BLACK", "isQueen":false},
//         {"x":5, "y":5, "color":"BLACK", "isQueen":false},
//         {"x":0, "y":6, "color":"WHITE", "isQueen":false},
//         {"x":1, "y":7, "color":"BLACK", "isQueen":false},
//         {"x":3, "y":7, "color":"BLACK", "isQueen":false}
//     ],
//     "Width":8,
//     "Height":8
// }`;

// const server : string = "http://localhost:8080";

// let board = connection.parseBoardJson(boardJson);

// client.createHTMLBoard(board.height,board.width);
// client.drawPieces(board.pieces);

