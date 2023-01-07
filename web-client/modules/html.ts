namespace html{
    export let settings : Map<string,string> = new Map<string,string>();

    export function startWindow(){  // Create HTML div with id="startWindow" inside div with id="content"
        const textField = document.getElementById('address') as HTMLInputElement;
        const variantSelector = document.getElementById('variant') as HTMLSelectElement;
        const colorSelector = document.getElementById('color') as HTMLSelectElement;
        const button = document.getElementById('connect') as HTMLButtonElement;
        const log = document.getElementById('log') as HTMLDivElement;
        const hidelog = document.getElementById('hide-log') as HTMLButtonElement;
        
        textField.setAttribute('value',connection.serverUrl);
        variants.Names.forEach(
            value => {
                const option = document.createElement('option');
                option.setAttribute('value',value);
                option.innerText = value;
                variantSelector.appendChild(option);
            }
        );
        variantSelector.onchange = ()=>{
            colorSelector.innerHTML = "";
            variants.Colors.get(variantSelector.value).forEach(
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