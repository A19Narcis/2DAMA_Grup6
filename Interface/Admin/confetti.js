
const button = document.getElementById('confetti')
const jsConfetti = new JSConfetti()


 button.addEventListener('click', () => {
  jsConfetti.addConfetti({
    emojis: ['🥵'],
  emojiSize: 100,
  confettiNumber: 30,
  });
  return;
});

