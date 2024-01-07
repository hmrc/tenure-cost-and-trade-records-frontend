const otherCostsItems = ['managers-and-staff', 'directors-remuneration']

const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '');
}

function calculateTotal(idx) {
    const total = otherCostsItems
        .map(item => Number(document.getElementById('totalPayrollCosts[' + idx + '].' + item).value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0);
    document.getElementById('payrollTotal.' + idx).innerText = asMoney(total);
}

[0, 1, 2].filter(idx => document.getElementById('payrollTotal.' + idx)).map(idx => {
    otherCostsItems.map(item => {
        const inputField = document.getElementById('totalPayrollCosts[' + idx + '].' + item)
        inputField.addEventListener('keyup', _ => calculateTotal(idx));
        inputField.addEventListener('blur', _ => calculateTotal(idx));
    })
    calculateTotal(idx);
});
