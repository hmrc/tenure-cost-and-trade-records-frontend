const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
})

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '')
}

function calculateTotal(inputFields, totalElement, isMoney) {
    const total = inputFields
        .map(inputField => Number(inputField.value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0)
    if (isMoney) {
        totalElement.innerText = asMoney(total)
    } else {
        totalElement.innerText = total
    }
}

document.querySelectorAll('[data-total-items]')
    .forEach(totalElement => {
        const isMoney = totalElement.getAttribute("data-total-is-money") !== "false"
        const prefix = totalElement.getAttribute("data-total-prefix")
        const items = totalElement.getAttribute("data-total-items")
        const inputFields = items.split(',')
            .map(item => document.getElementById(prefix + item))

        inputFields.forEach(inputField => {
            inputField.addEventListener('keyup', _ => calculateTotal(inputFields, totalElement, isMoney))
            inputField.addEventListener('blur', _ => calculateTotal(inputFields, totalElement, isMoney))
        })
        calculateTotal(inputFields, totalElement, isMoney)
    })
