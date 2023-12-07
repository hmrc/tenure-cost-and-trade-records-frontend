const otherCostsItems = ['contributionsToHeadOffice', 'otherCosts']

const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '');
}

function calculateTotal(idx) {
    const total = otherCostsItems
        .map(item => Number(document.getElementById('otherCosts[' + idx + '].' + item).value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0);
    document.getElementById('otherCostTotal.' + idx).innerText = asMoney(total);
}

[0, 1, 2].filter(idx => document.getElementById('otherCostTotal.' + idx)).map(idx => {
    otherCostsItems.map(item => {
        const inputField = document.getElementById('otherCosts[' + idx + '].' + item)
        inputField.addEventListener('keyup', _ => calculateTotal(idx));
        inputField.addEventListener('blur', _ => calculateTotal(idx));
    })
    calculateTotal(idx);
});
