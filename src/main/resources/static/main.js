'use strict';

let stompClient = null;
/* Selectors */
let startGameForm = document.querySelector('#startGameForm');
let gameForm = document.querySelector('#gameForm');
let startGame = document.querySelector('#start-game');
let gameBoard = document.querySelector('#game-board');
let dividersDropdown = document.querySelector('#dividers-dropdown');
let numbersDropdown = document.querySelector('#numbers-dropdown');
let gameOver = document.querySelector('#game-over');
let gameInterrupted = document.querySelector('#game-interrupted');
let singlePlayer = document.querySelector('#singlePlayer');
let result;
let initialMove;

/* Connect to the game ws */
function connect(event) {
    let socket = new SockJS('/gameWs');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

    event.preventDefault();
}

/* Subscribe to queue */
function onConnected(event) {
    startGame.classList.add('hidden');
    gameBoard.classList.remove('hidden');
    let type = $('#singlePlayer').is(":checked") ? 'START_GAME_SINGLE' : 'START_GAME'

    stompClient.subscribe('/user/queue/public', onMessageReceived);

    let message = {
        sender: '',
        content: '',
        type: type
    };
    stompClient.send("/app/play.addUser", {}, JSON.stringify(message));
}

/* Send messages to the queue */
function sendMessage(event) {
    let divider = $('#dividers').val();
    let number = $('#numbers').val();

    let content = JSON.stringify({
        divider: divider ? divider : '',
        number: number ? number : '',
        result: result ? result : ''
    });
    let message = {
        content: content,
        type: 'MAKE_MOVE'
    };
    stompClient.send("/app/play.makeMove", {}, JSON.stringify(message));
    // if (initialMove) {
    //     numbersDropdown.classList.add('hidden');
    // }
    // initialMove = false;
    event.preventDefault();
}


/* Message handler */
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    const messageType = message.type;
    const content = getContent(payload);
    switch (messageType) {
        case "START_GAME": {
            if (!content.hasTwoPlayers) {
                //show dividers to first player
                dividersDropdown.classList.remove('hidden');
                initialMove = true;
            } else {
                $('#dividers').val(content.divider);
                //hide divider
                dividersDropdown.classList.add('hidden');
                //show numbers
                populateNumbers(content.numbers);
                if (content.result) {
                    numbersDropdown.classList.remove('hidden');
                }
                //show result
                populateResult(content);
            }
        }
            break;
        case "START_GAME_SINGLE": {
            $('#dividers').val(content.divider);
            //hide divider
            dividersDropdown.classList.remove('hidden');
            populateResult(content);
        }
            break;
        case "MAKE_MOVE": {
            //hide dividers from first player
            dividersDropdown.classList.add('hidden');
            //hide or show numbers
            populateNumbers(content.numbers);
            if (content.result && content.hasTwoPlayers) {
                numbersDropdown.classList.contains('hidden') ? numbersDropdown.classList.remove('hidden') : numbersDropdown.classList.add('hidden');
            }
            if (initialMove) {
                numbersDropdown.classList.add('hidden');
                initialMove = false;
            }
            //show result
            populateResult(content);
        }
            break;
        case "GAME_OVER": {

            gameOver.classList.remove('hidden');
            numbersDropdown.classList.add('hidden');
            dividersDropdown.classList.add('hidden');
        }
            break;
        case "GAME_INTERRUPTED": {
            gameInterrupted.classList.remove('hidden');
            gameBoard.classList.add('hidden');
        }
            break;
        case "WIN": {
            //show result
            populateResult(content);
            //show final message
            let node = document.createElement("LI");
            let textNode = document.createTextNode(numbersDropdown.classList.contains('hidden') ? "You lose" : "You win");
            node.appendChild(textNode);
            document.getElementById("resultList").appendChild(node);
        }
            break;
        default:
            console.log("Something went wrong");
    }
}

function getContent(payload) {
    return JSON.parse(JSON.parse(JSON.parse(payload.body).content).content);
}

function populateNumbers(numbers) {
    $("#numbers").empty();
    numbers && numbers.forEach(n => $("#numbers").append('<option value="' + n + '">' + n + '</option>'));
}

function populateResult(content) {
    let node = document.createElement("LI");
    result = content.result;
    let textNode = document.createTextNode("The divider: " + (content.divider || content.divider === 0 ? content.divider : "") + "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0 Number: " + (content.number || content.number === 0 ? content.number : "") + "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0 Result: " + (content.result || content.result === 0 ? content.result : ""));
    node.appendChild(textNode);
    document.getElementById("resultList").appendChild(node);

}

/* Error handler */
function onError(error) {
    console.log(error);
}

/* Event listeners */
startGameForm.addEventListener('submit', connect, true);
gameForm.addEventListener("submit", sendMessage, true);

