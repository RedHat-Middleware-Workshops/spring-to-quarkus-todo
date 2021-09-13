
document.addEventListener("DOMContentLoaded", function(){
    fetch('/todo')
    .then(response => response.json())
    .then(arr => {
        var todos = document.getElementById("todos");

        arr.forEach(element => {
            var todoEle = document.createElement("todo-component");
            todoEle.setAttribute("id", element.id);
            todoEle.setAttribute("title", element.title);
            if(element.completed == true) {
                todoEle.setAttribute("completed", 'true');
            }
            todos.appendChild(todoEle);
        });
    })
    .catch(err => console.error(err));
});