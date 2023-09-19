const costOfSalesItems = ['accommodation', 'food', 'drinks', 'other']

const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '');
}

function calculateTotal(idx) {
    const total = costOfSalesItems
        .map(item => Number(document.getElementById('costOfSales[' + idx + '].' + item).value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0);
    document.getElementById('total.' + idx).innerText = asMoney(total);
}

[0, 1, 2].filter(idx => document.getElementById('total.' + idx)).map(idx => {
    costOfSalesItems.map(item => {
        const inputField = document.getElementById('costOfSales[' + idx + '].' + item)
        inputField.addEventListener('keyup', _ => calculateTotal(idx));
        inputField.addEventListener('blur', _ => calculateTotal(idx));
    })
    calculateTotal(idx);
});
