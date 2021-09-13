class TodoComponent extends HTMLElement {

    get id() {
        return this.get('value','id');
    }

    set id(id) {
        this.set('value','id',id)
    }

    get title() {
        return this.get('value','title');
    }

    set title(title) {
        this.set('value','title',title)
    }

    get completed() {
        return this.get('checked','completed');
    }

    set completed(val) {
        this.set('checked','completed',val)
    }

    // Get the property from either the attribute or the form field
    get(prop, name) {
        const ele = this.form.elements[name];
        if (ele[prop] == '') {
            if(this.getAttribute(name) != null) {
                ele[prop] = decodeURI(this.getAttribute(name));
            }
        }
        return ele[prop];
    }

    // Set the property to the attribute and the form field
    set(prop, name, val) {
        this.form.elements[name][prop] = val;
        this.setAttribute(name, encodeURI(val));
    }

    constructor() {
        super();
        this.attachShadow({mode: 'open'});

        fetch('./todo-component.html')
        .then(stream => stream.text())
        .then(html => {
            const tmpl = document.createElement('template');
            tmpl.innerHTML = html;
            this.shadowRoot.appendChild(tmpl.content.cloneNode(true));
            this.initialize();
        });
    }

    initialize() {
        const todo = this;
        this.form = this.shadowRoot.querySelector('form');

        // Initialize the form fields by calling the getters
        this.id;
        this.title;
        this.completed;

        this.form.elements['delete'].addEventListener('click', function(e) {
            fetch('/todo/' + todo.id, {
                method: 'DELETE'
            })
            .then(response => {
                todo.remove();
            })
            .catch(err => console.error(err));
        });
        
        this.form.elements['title'].addEventListener('keyup', function(e) {
            e.preventDefault();
            if (e.keyCode === 13) {
                new FormData(todo.form);
                e.target.blur();
            }
        });

        this.form.elements['completed'].addEventListener('click', (e) => {
            new FormData(todo.form);
        });

        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
        });
        
        this.form.addEventListener('formdata', (e) => {
            e.preventDefault();
            const data = Object.fromEntries(e.formData);

            todo.title = data.title;
            todo.completed = data.completed;

            if(data.id == '') { // New Todo
                fetch('/todo', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(data)
                })
                .then(response => response.json())
                .then(data => {
                    todo.id = data.id;
                    console.log('Added: ' + JSON.stringify(data));
                })
                .catch(err => console.error(err));

                var newTodo = document.createElement('todo-component');
                todo.parentElement.prepend(newTodo);
            }
            else { // Update Todo
                // Convert completed to boolean value
                (data.completed == 'on') ? data.completed = true : data.completed = false;
                fetch('/todo', {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(data)
                })
                .catch(err => console.error(err));
            }
        });
    }
}

window.customElements.define('todo-component', TodoComponent);