HTMLInputElement.prototype.focusAndSelect = function () {
    this.focus();
    this.select();
}

const cover = {
    createIfNotExists: () => {
        if (cover.getElement() !== null) {
            return;
        }
        const coverImageElement = window.document.createElement('img');
        coverImageElement.classList.add('_image');
        coverImageElement.setAttribute('alt', '');
        coverImageElement.setAttribute('src', '/resources/images/_loading.png');
        const coverMessageElement = window.document.createElement('span');
        coverMessageElement.classList.add('_message');
        const coverElement = window.document.createElement('div');
        coverElement.classList.add('_object-cover');
        coverElement.append(coverImageElement, coverMessageElement);
        window.document.body.append(coverElement);
    },
    hide: () => {
        window.document.body.querySelectorAll('[data-disabled-by-cover]').forEach(input => {
            if (input.disabled) {
                input.removeAttribute('disabled');
                delete input.dataset.disabledByCover;
            }
        });
        window.document.body.classList.remove('_covering');
    },
    getElement: () => window.document.body.querySelector(':scope > ._object-cover'),
    isShown: () => window.document.body.classList.contains('_covering'),
    show: (message) => {
        window.document.body.querySelectorAll('input').forEach(input => {
            if (!input.disabled) {
                input.setAttribute('disabled', 'disabled');
                input.dataset.disabledByCover = 'yes';
            }
        });
        cover.createIfNotExists();
        const messageElement = cover.getElement().querySelector('._message');
        if (message === undefined) {
            messageElement.style.display = 'none';
        } else {
            messageElement.innerText = message;
            messageElement.style.display = 'block';
        }
        window.document.body.classList.add('_covering');
    }
};


const _writeButton = window.document.getElementById('_writeButton');
const _writeMenu = window.document.getElementById('_writeMenu');

_writeButton?.addEventListener('click', () => {
    if (_writeMenu?.classList.contains('visible')) {
        _writeMenu?.classList.remove('visible')
    } else {
        _writeMenu?.classList.add('visible');
    }
});

_writeMenu?.addEventListener('mouseleave', () => {
    _writeMenu?.classList.remove('visible');
})