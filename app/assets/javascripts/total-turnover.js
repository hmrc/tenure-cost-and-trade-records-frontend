const turnoverItems = ['accommodation', 'food', 'alcoholic-drinks', 'other-receipts'];

const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '');
}

function calculateTotalTurnover(idx) {
    const total = turnoverItems
        .map(item => Number(document.getElementById(idx + '.' + item).value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0);
    document.getElementById('total-sales-revenue-' + idx).innerText = asMoney(total);
}

[0, 1, 2] // Assuming you have 3 financial years, adjust this array as needed
    .filter(idx => document.getElementById('total-sales-revenue-' + idx))
    .forEach(idx => {
        turnoverItems.forEach(item => {
            const inputField = document.getElementById(idx + '.' + item);
            if (inputField) {
                inputField.addEventListener('keyup', _ => calculateTotalTurnover(idx));
                inputField.addEventListener('blur', _ => calculateTotalTurnover(idx));
            }
        });
        calculateTotalTurnover(idx); // Initial calculation
    });
