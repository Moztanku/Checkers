namespace connection{
    export let serverUrl = "ws://localhost:8080";
    let socket : WebSocket;

    export function ConnectToServer(){
        socket = new WebSocket(serverUrl);

        socket.onopen = (event: Event) => {
            console.log("Connected to server");
            html.log("Connected to server");

            SendRequest(JsonParser.SetVariant(html.settings.get("variant")));
            SendRequest(JsonParser.SetColor(html.settings.get("color")));
        }

        socket.onmessage = event => {
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