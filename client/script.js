const socket = io("http://localhost:9092");

let currentPlayer = "Player 1";
let gameStarted = false;

// Event listeners
document.getElementById("start-button").addEventListener("click", handleStartButtonClick);
// document.getElementById("move-minus-one").addEventListener("click", () => handleManualPlayerMove("-1"));
// document.getElementById("move-zero").addEventListener("click", () => handleManualPlayerMove("0"));
// document.getElementById("move-plus-one").addEventListener("click", () => handleManualPlayerMove("1"));

// Socket event handlers
socket.on("connect", handleSocketConnect);
socket.on("random-number", handleRandomNumber);
socket.on("game-started", handleGameStarted);
socket.on("game-over", handleGameOver);
socket.on("game-state", handleGameState);
// socket.on("player-move", handleManualPlayerMove);
socket.on("waiting-for-player-2", handleWaitingForPlayer2);
socket.on("game-full", handleGameFull);
socket.on('disconnect', (reason) => {
	console.log(`Disconnected: ${reason}`);
	socket.disconnect();
})

// Event handler functions
function handleStartButtonClick() {
	if (!gameStarted) {
		const currentPlayerId = socket.id;
		socket.emit("start-game", currentPlayerId);
		gameStarted = true;
	}
}

function handleManualPlayerMove(move) {
	socket.emit("player-move", move);
}

function handleSocketConnect() {
	console.log("Connected to server");
	console.log(socket.id);
}

function handleRandomNumber(randomNumber) {
	const playerInfo = document.getElementById("player-info");
	playerInfo.innerHTML += `<p>${currentPlayer}: ${randomNumber}</p>`;
	playerInfo.scrollTop = playerInfo.scrollHeight;

	currentPlayer = currentPlayer === "Player 1" ? "Player 2" : "Player 1";

	disableStartButton();
}

function handleGameStarted(startingPlayerId) {
	document.getElementById("game-info").innerText = "";

	const isStartingPlayer = startingPlayerId === socket.id;

	if (isStartingPlayer) {
		document.getElementById("turn-info").innerText = "Opponent's turn";
		// disableMoveButtons();
	} else {
		document.getElementById("turn-info").innerText = "Your turn";
		// enableMoveButtons();
	}
}

function handleGameOver(message) {
	document.getElementById("game-info").innerText = message;
	// disableMoveButtons();
}

function handleGameState(gameState) {
	const currentNumber = gameState.currentNumber;
	const currentPlayerId = gameState.currentPlayer;

	const playerInfo = document.getElementById("player-info");
	playerInfo.innerHTML += `<p>${currentPlayer}: ${currentNumber}</p>`;
	playerInfo.scrollTop = playerInfo.scrollHeight;

	if (currentPlayerId === socket.id) {
		document.getElementById("turn-info").innerText = "Opponent's turn";
		// disableMoveButtons();
	} else {
		document.getElementById("turn-info").innerText = "Your turn";
		// enableMoveButtons();
	}
}

function handleWaitingForPlayer2() {
	document.getElementById("game-info").innerText = "Waiting for Player 2 to join...";
}

function handleGameFull(message) {
	alert(message);
}

// Utility functions
function disableStartButton() {
	document.getElementById("start-button").disabled = true;
}

function enableStartButton() {
	document.getElementById("start-button").disabled = false;
}

// function disableMoveButtons() {
// 	document.getElementById("move-minus-one").disabled = true;
// 	document.getElementById("move-zero").disabled = true;
// 	document.getElementById("move-plus-one").disabled = true;
// }

// function enableMoveButtons() {
// 	document.getElementById("move-minus-one").disabled = false;
// 	document.getElementById("move-zero").disabled = false;
// 	document.getElementById("move-plus-one").disabled = false;
// }

