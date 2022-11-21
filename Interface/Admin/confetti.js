const button = document.getElementById('confetti');
const button2 = document.getElementById('conffetti');

const jsConfetti = new JSConfetti();


 button.addEventListener('click', () => {
  jsConfetti.addConfetti({
    emojiSize: 70,
    confettiNumber: 305,
  });
  return;
});
button2.addEventListener('click', () => {
  jsConfetti.addConfetti({
    emojis: ['🤡', '🤬'],
    emojiSize: 70,
    confettiNumber: 305,
  });
  return;
});

