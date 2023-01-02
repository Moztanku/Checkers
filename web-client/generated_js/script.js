var Piece = /** @class */ (function () {
    function Piece(x, y, color, isQueen) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.isQueen = isQueen;
    }
    return Piece;
}());
var Board = /** @class */ (function () {
    function Board(pieces, width, height) {
        this.pieces = pieces;
        this.width = width;
        this.height = height;
    }
    return Board;
}());
function tdClicked(x, y) {
    console.log("td clicked at x:".concat(x, " y:").concat(y));
}
function createHTMLBoard(height, width) {
    var _a;
    var table = document.createElement('table');
    table.setAttribute('id', 'board');
    var _loop_1 = function (y) {
        var tr = table.insertRow();
        var _loop_2 = function (x) {
            var ce = tr.insertCell();
            ce.setAttribute('color', ((x + y) % 2 === 0) ? 'black' : 'white');
            ce.setAttribute('piece', 'NONE');
            ce.onclick = function () {
                tdClicked(x, y);
            };
            ce.setAttribute('x', x.toString());
            ce.setAttribute('y', y.toString());
        };
        for (var x = 0; x < width; x++) {
            _loop_2(x);
        }
    };
    for (var y = 0; y < height; y++) {
        _loop_1(y);
    }
    (_a = document.getElementById("content")) === null || _a === void 0 ? void 0 : _a.appendChild(table);
}
function getCell(x, y) {
    return document.querySelector("td[x=\"".concat(x, "\"][y=\"").concat(y, "\"]"));
}
;
function drawPieces(pieces) {
    pieces.forEach(function (value) {
        var cell = getCell(value.x, value.y);
        cell === null || cell === void 0 ? void 0 : cell.setAttribute('piece', value.color);
    });
}
function parseBoardJson(boardJson) {
    var board = JSON.parse(boardJson);
    var pieces = [];
    board.Pieces.forEach(function (value) {
        pieces.push(new Piece(value.x, value.y, value.color, value.isQueen));
    });
    var width = board.Width;
    var height = board.Height;
    return new Board(pieces, width, height);
}
var boardJson = "{\n    \"Pieces\":[\n        {\"x\":0, \"y\":0, \"color\":\"WHITE\" ,\"isQueen\":false},\n        {\"x\":2, \"y\":0, \"color\":\"WHITE\" ,\"isQueen\":false},\n        {\"x\":0, \"y\":7, \"color\":\"BLACK\" ,\"isQueen\":false},\n        {\"x\":2, \"y\":7, \"color\":\"BLACK\" ,\"isQueen\":false}\n    ],\n    \"Width\":8,\n    \"Height\":8\n}";
var board = parseBoardJson(boardJson);
createHTMLBoard(8, 8);
drawPieces(board.pieces);
