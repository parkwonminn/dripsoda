const form = window.document.getElementById('form');
const warning = {
    getElement: () => window.document.getElementById('warning'),
    hide: () => warning.getElement().classList.remove('visible'),
    show: (text) => {
        warning.getElement().innerText = text;
        warning.getElement().classList.add('visible');
    }
};

form.onsubmit = e => {
    e.preventDefault();

    if (form['password'].value === '') {
        warning.show('새로운 비밀번호를 입력해 주세요.');
        form['password'].focus();
        return false;
    }
    if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:\'\",<.>/?]{8,50})$').test(form['password'].value)) {
        warning.show('올바른 비밀번호를 입력해 주세요.');
        form['password'].focusAndSelect();
        return false;
    }
    if (form['passwordCheck'].value === '') {
        warning.show('새로운 비밀번호를 다시 입력해 주세요.');
        form['passwordCheck'].focus();
        return false;
    }
    if (form['password'].value !== form['passwordCheck'].value) {
        warning.show('비밀번호가 일치하지 않습니다.');
        form['passwordCheck'].focusAndSelect();
        return false;
    }
    cover.show('비밀번호를 다시 설정하고 있습니다.\n\n잠시만 기다려 주세요.');

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('password', form['password'].value);
    xhr.open('POST', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseJson = JSON.parse(xhr.responseText);
                switch (responseJson['result']) {
                    case 'expired':
                        warning.show('해당 링크가 이미 사용되었거나 만료되었습니다. 비밀번호를 재설정하지 못한 경우 비밀번호 재설정 기능을 다시 이용해 주세요.');
                        break;
                    case 'success':
                        form.classList.remove('visible');
                        window.document.getElementById('result').classList.add('visible');
                        break;
                    default:
                        warning.show('비밀번호를 재설정 하지 못하였습니다. 링크가 잘못되었거나 더 이상 사용할 수 없을 수도 있습니다.');
                }
            } else {
                warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};