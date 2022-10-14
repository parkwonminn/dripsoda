const infoForm = window.document.getElementById('infoForm');
const warning = {
    getElement: () => window.document.getElementById('warning'),
    hide: () => warning.getElement().classList.remove('visible'),
    show: (text) => {
        warning.getElement().innerText = text;
        warning.getElement().classList.add('visible');
    }
};

infoForm?.changePassword?.addEventListener('input', () => {
    infoForm.querySelectorAll('[rel="row-change-password"]').forEach(x => {
        if (infoForm['changePassword'].checked) {
            x.classList.add('visible');
            infoForm['newPassword'].value = '';
            infoForm['newPasswordCheck'].value = '';
            infoForm['newPassword'].focus();
        } else {
            x.classList.remove('visible');
        }
    });
});

infoForm?.changeContact?.addEventListener('input', () => {
    infoForm.querySelectorAll('[rel="row-change-contact"]').forEach(x => {
        if (infoForm['changeContact'].checked) {
            x.classList.add('visible');
            infoForm['newContactAuthSalt'].value = '';
            infoForm['newContact'].value = '';
            infoForm['newContact'].removeAttribute('disabled');
            infoForm['newContactAuthRequestButton'].removeAttribute('disabled');
            infoForm['newContactAuthCode'].value = '';
            infoForm['newContactAuthCode'].setAttribute('disabled', 'disabled');
            infoForm['newContactAuthCheckButton'].setAttribute('disabled', 'disabled');
            infoForm['newContact'].focus();
        } else {
            x.classList.remove('visible');
        }
    });
});

infoForm?.newContactAuthRequestButton?.addEventListener('click', () => {
    warning.hide();
    if (infoForm['newContact'].value === '') {
        warning.show('새로운 연락처를 입력해 주세요.');
        infoForm['newContact'].focus();
        return;
    }
    if (!new RegExp('^(\\d{8,12})$').test(infoForm['newContact'].value)) {
        warning.show('올바른 새로운 연락처를 입력해 주세요.')
        infoForm['newContact'].focusAndSelect();
        return;
    }
    cover.show('인증번호를 전송하고 있습니다.\n\n잠시만 기다려 주세요.');

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `./userMyInfoAuth?newContact=${infoForm['newContact'].value}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseJson = JSON.parse(xhr.responseText);
                switch (responseJson['result']) {
                    case 'duplicate':
                        warning.show('해당 연락처는 이미 사용 중입니다.');
                        infoForm['newContact'].focusAndSelect();
                        break;
                    case 'success':
                        warning.show('입력하신 연락처로 인증번호를 포함한 문자를 전송하였습니다. 5분 내로 문자로 전송된 인증번호를 확인해 주세요.');
                        infoForm['newContactAuthSalt'].value = responseJson['salt'];
                        infoForm['newContact'].setAttribute('disabled', 'disabled');
                        infoForm['newContactAuthRequestButton'].setAttribute('disabled', 'disabled');
                        infoForm['newContactAuthCode'].removeAttribute('disabled');
                        infoForm['newContactAuthCheckButton'].removeAttribute('disabled');
                        infoForm['newContactAuthCode'].focusAndSelect();
                        break;
                    default:
                        warning.show('알 수 없는 이유로 인증번호를 전송하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        infoForm['newContact'].focusAndSelect();
                }
            } else {
                warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                infoForm['newContact'].focusAndSelect();
            }
        }
    };
    xhr.send();
});

infoForm?.newContactAuthCheckButton?.addEventListener('click', () => {
    warning.hide();
    if (infoForm['newContactAuthCode'].value === '') {
        warning.show('인증번호를 입력해 주세요.');
        infoForm['newContactAuthCode'].focus();
        return;
    }
    if (!new RegExp('^(\\d{6})$').test(infoForm['newContactAuthCode'].value)) {
        warning.show('올바른 인증번호를 입력해 주세요.');
        infoForm['newContactAuthCode'].focusAndSelect();
        return;
    }
    cover.show('인증번호를 확인하고 있습니다.\n\n잠시만 기다려 주세요.');

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('newContact', infoForm['newContact'].value);
    formData.append('newContactAuthCode', infoForm['newContactAuthCode'].value);
    formData.append('newContactAuthSalt', infoForm['newContactAuthSalt'].value);
    xhr.open('POST', './userMyInfoAuth');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseJson = JSON.parse(xhr.responseText);
                switch (responseJson['result']) {
                    case 'expired':
                        warning.show('입력한 인증번호가 만료되었습니다. 인증번호를 다시 요청하여 인증해 주세요.');
                        infoForm['newContact'].removeAttribute('disabled');
                        infoForm['newContactAuthRequestButton'].removeAttribute('disabled');
                        infoForm['newContactAuthCode'].value = '';
                        infoForm['newContactAuthCode'].setAttribute('disabled', 'disabled');
                        infoForm['newContactAuthCheckButton'].setAttribute('disabled', 'disabled');
                        infoForm['newContactAuthSalt'].value = '';
                        infoForm['newContact'].focusAndSelect();
                        break;
                    case 'success':
                        warning.show('연락처가 성공적으로 인증되었습니다.');
                        infoForm['newContactAuthCode'].setAttribute('disabled', 'disabled');
                        infoForm['newContactAuthCheckButton'].setAttribute('disabled', 'disabled');
                        break;
                    default:
                        warning.show('입력한 인증번호가 올바르지 않습니다.');
                        infoForm['newContactAuthCode'].focusAndSelect();
                }
            } else {
                warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                infoForm['newContact'].focusAndSelect();
            }
        }
    };
    xhr.send(formData);
});

if (infoForm !== null) {
    infoForm.onsubmit = e => {
        e.preventDefault();

        if (!infoForm['changePassword'].checked && !infoForm['changeContact'].checked) {
            warning.show('변경할 내용이 없습니다.');
            return false;
        }
        if (infoForm['oldPassword'].value === '') {
            warning.show('현재 비밀번호를 입력해 주세요.');
            infoForm['oldPassword'].focus();
            return false;
        }
        if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:\'\",<.>/?]{8,50})$').test(infoForm['oldPassword'].value)) {
            warning.show('올바른 현재 비밀번호를 입력해 주세요.');
            infoForm['oldPassword'].focusAndSelect();
            return false;
        }
        if (infoForm['changePassword'].checked) {
            if (infoForm['newPassword'].value === '') {
                warning.show('새로운 비밀번호를 입력해 주세요.');
                infoForm['newPassword'].focus();
                return false;
            }
            if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:\'\",<.>/?]{8,50})$').test(infoForm['newPassword'].value)) {
                warning.show('올바른 새로운 비밀번호를 입력해 주세요.');
                infoForm['newPassword'].focusAndSelect();
                return false;
            }
            if (infoForm['newPasswordCheck'].value === '') {
                warning.show('새로운 비밀번호를 다시 입력해 주세요.');
                infoForm['newPasswordCheck'].focus();
                return false;
            }
            if (infoForm['newPassword'].value !== infoForm['newPasswordCheck'].value) {
                warning.show('새로운 비밀번호가 서로 일치하지 않습니다.');
                infoForm['newPasswordCheck'].focusAndSelect();
                return false;
            }
        }
        if (infoForm['changeContact'].checked) {
            if (!infoForm['newContactAuthRequestButton'].disabled || !infoForm['newContactAuthCheckButton'].disabled) {
                warning.show('변경할 새로운 연락처에 대한 인증을 완료해 주세요.');
                return false;
            }
        }
        cover.show('회원정보를 변경하고 있습니다.\n\n잠시만 기다려 주세요.');

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('oldPassword', infoForm['oldPassword'].value);
        formData.append('changePassword', infoForm['changePassword'].checked);
        formData.append('changeContact', infoForm['changeContact'].checked);
        if (infoForm['changePassword'].checked) {
            formData.append('newPassword', infoForm['newPassword'].value);
        }
        if (infoForm['changeContact'].checked) {
            formData.append('newContact', infoForm['newContact'].value);
            formData.append('newContactAuthCode', infoForm['newContactAuthCode'].value);
            formData.append('newContactAuthSalt', infoForm['newContactAuthSalt'].value);
        }
        xhr.open('POST', './userMyInfo');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                cover.hide();
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseJson = JSON.parse(xhr.responseText);
                    switch (responseJson['result']) {
                        case 'success':
                            alert('회원정보를 성공적으로 수정하였습니다.');
                            window.location.reload();
                            break;
                        default:
                            warning.show('현재 비밀번호가 일치하지 않습니다. 다시 확인해 주세요.');
                            infoForm['oldPassword'].focusAndSelect();
                    }
                } else {
                    warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            }
        };
        xhr.send(formData);

        // 값을 넘겨 받은 서버는 :
        //      - 비밀번호 변경하기가 체크되어 있었다면
        //          - 로그인한 사용자의 비밀번호와 해싱한 'oldPassword'가 같은지 확인한 뒤 다르다면 FAILURE를 반환한다.
        //          - 로그인한 사용자의 비밀번호를 해싱한 'newPassword'로 지정한다.
        //      - 연락처 변경하기가 체크되어 있다면
        //          - 넘겨받은 'newContact', 'newContactAuthCode', 'newContactAuthSalt'를 통해 ContactAuth 레코드가 있는지 조회한 뒤 없다면 FAILURE를 반환한다.
        //          - 로그인한 사용자의 연락처를 넘겨 받은 'newContact'로 지정한다.
        //          - 사용자 정보를 업데이트 한다.
        //          - SUCCESS 를 반환한다.
    };
}











