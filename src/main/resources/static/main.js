'use strict';

let stompClient = null;
/* Selectors */
let startGameForm = document.querySelector('#startGameForm');
let gameForm = document.querySelector('#gameForm');
let startGame = document.querySelector('#start-game');
let gameBoard = document.querySelector('#game-board');
let dividersDropdown = document.querySelector('#dividers-dropdown');
let gameOver = document.querySelector('#game-over');

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

    stompClient.subscribe('/user/queue/public', onMessageReceived);
    let message = {
        sender: '',
        content: '',
        type: 'START_GAME'
    };
    stompClient.send("/app/play.addUser", {}, JSON.stringify(message));
}

/* Send messages to the queue */
function sendMessage(event) {

    let gameMessage = {
        content: "",
        type: "type"
    };
    stompClient.send("/app/play.sendMessage", {}, JSON.stringify(gameMessage));

    event.preventDefault();
}


/* Message handler */
function onMessageReceived(payload) {
    console.log(payload);
    let message = JSON.parse(payload.body);
    const messageType = message.type;
    const content = JSON.parse(message.content);
    if (messageType === "START_GAME") {
        if (content.content === "1") dividersDropdown.classList.remove('hidden');
    } else if (messageType === "GAME_INTERRUPTED") {
        gameOver.classList.remove('hidden');
    }
}

/* Error handler */
function onError(error) {
    // connectingElement.textContent = "Something went wrong. Refresh and try again!";
    // connectingElement.style.color = "red";
}

/* Event listeners */
startGameForm.addEventListener('submit', connect, true);
gameForm.addEventListener("submit", sendMessage, true);

