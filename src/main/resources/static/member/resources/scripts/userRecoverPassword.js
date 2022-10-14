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



    if (form['email'].value === '') {
        warning.show('이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return false;
    }
    if (!new RegExp('^(?=.{7,50})([\\da-zA-Z_.]{4,})@([\\da-z\\-]{2,}\\.)?([\\da-z\\-]{2,})\\.([a-z]{2,10})(\\.[a-z]{2})?$').test(form['email'].value)) {
        warning.show('올바른 이메일을 입력해 주세요.');
        form['email'].focusAndSelect();
        return false;
    }
    cover.show('비밀번호 재설정 링크를 전송하고 있습니다.\n\n잠시만 기다려 주세요.');

    const xhr = new XMLHttpRequest();
    xhr.open('GET', './userRecoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseJson = JSON.parse(xhr.responseText);
                switch (responseJson['result']) {
                    case 'duplicate':
                        registerWarning.show('해당 연락처는 이미 사용 중입니다.');
                        registerForm['newContact'].focusAndSelect();
                        break;
                    case 'success':
                        registerWarning.show('입력하신 연락처로 인증번호를 포함한 문자를 전송하였습니다. 5분 내로 문자로 전송된 인증번호를 확인해 주세요.');
                        registerForm['newContactAuthSalt'].value = responseJson['salt'];
                        registerForm['newContact'].setAttribute('disabled', 'disabled');
                        registerForm['newContactAuthRequestButton'].setAttribute('disabled', 'disabled');
                        registerForm['newContactAuthCode'].removeAttribute('disabled');
                        registerForm['newContactAuthCheckButton'].removeAttribute('disabled');
                        registerForm['newContactAuthCode'].focusAndSelect();
                        break;
                    default:
                        registerWarning.show('알 수 없는 이유로 인증번호를 전송하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        registerForm['newContact'].focusAndSelect();
                }
            } else {
                registerWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                registerForm['contact'].focusAndSelect();
            }
        }
    };
    xhr.send();
};













