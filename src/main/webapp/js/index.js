
function goToSearch(){
    let query = document.querySelector("#search-input").value;
    window.location.href = `./search?q=${query}`;
}

document.querySelector("#search-btn").addEventListener('click', (event) => {
    goToSearch();
});

document.querySelector("#search-input").addEventListener('keypress', (event)=>{
    // event.keyCode or event.which  property will have the code of the pressed key
    let keyCode = event.keyCode ? event.keyCode : event.which;

    // 13 points the enter key
    if(keyCode === 13) {
        // call click function of the buttonn
        goToSearch();
    }
});