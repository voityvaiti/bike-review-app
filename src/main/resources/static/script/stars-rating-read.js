const blocks = document.querySelectorAll('.average-rating');
const amountOfStars = 5;
blocks.forEach(block => {
    const rating = block.value;
    const percent = (rating / amountOfStars) * 100;
    block.style.setProperty('--percent', `${percent}%`);
});