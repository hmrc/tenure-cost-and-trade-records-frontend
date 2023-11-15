function synchronizeRowsHeight() {
    const govukTables = document.getElementsByClassName('govuk-table');
    const tablesCount = govukTables.length;
    const rowsHeight = [];

    if (tablesCount > 1 && screen.width > 800) {
        for (let t = 0; t < tablesCount; t++) {
            const rows = govukTables[t].getElementsByTagName('tr');
            for (let r = 0; r < rows.length; r++) {
                rowsHeight[r] = rowsHeight.length > r && rowsHeight[r] > rows[r].offsetHeight ? rowsHeight[r] : rows[r].offsetHeight;
            }
        }
        for (let t = 0; t < tablesCount; t++) {
            const rows = govukTables[t].getElementsByTagName('tr');
            for (let r = 0; r < rows.length; r++) {
                rows[r].style.height = rowsHeight[r] + 'px';
            }
        }
    }
}

synchronizeRowsHeight();

window.addEventListener('resize', synchronizeRowsHeight);
