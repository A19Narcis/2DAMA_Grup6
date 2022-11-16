
const button = document.getElementById('confetti')
const jsConfetti = new JSConfetti()


 button.addEventListener('click', () => {
  jsConfetti.addConfetti({

  emojiSize: 70,
  confettiNumber: 305,
  });
  return;
});

