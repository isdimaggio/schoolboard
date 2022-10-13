// ========================================================
// =                    AVATAR CHANGE                     =
// ========================================================

function ppModalListener_change() {
    if (this.files && this.files[0]) {

        // check dimensions
        if (this.files[0].size > 2097152) {
            toastr.error(
                document.getElementById('changePpModal-opErr').textContent +
                " [CLIENT] " +
                document.getElementById('changePpModal-fileTooBig').textContent
            );
            this.value = "";
            return;
        }

        let reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById("changePpModal-img").src = e.target.result;
        };
        reader.readAsDataURL(this.files[0]);
    }
}

function ppModalListener_show() {
    let req = jQuery.ajax({
        url: '/api/profile-pictures/',
        method: 'GET'
    });

    req.then(function (response) {
        document.getElementById("changePpModal-img").src = response.s3Location;
    }, function (xhr) {
        console.log(xhr);
    })
}

function ppModalListener_submit() {
    // request
    let fd = new FormData();
    fd.append('profile-picture', changePpModalFile.files[0]);
    fd.append(CSRF_PARAM_NAME, CSRF_TOKEN); //csrf protection

    $('#changePpModal').modal('hide');

    let req = jQuery.ajax({
        url: '/api/profile-pictures/',
        method: 'POST',
        data: fd,
        processData: false,
        contentType: false
    });

    req.then(function () {
        toastr.success(
            document.getElementById('changePpModal-opOk').textContent
        );
    }, function (xhr) {
        toastr.error(
            document.getElementById('changePpModal-opErr').textContent +
            " [SERVER] " +
            xhr.responseText
        );
    })
}

function ppModalListener_delete() {
    // request
    $('#changePpModal').modal('hide');

    let req = jQuery.ajax({
        url: '/api/profile-pictures/?' + CSRF_PARAM_NAME + "=" + CSRF_TOKEN,
        method: 'DELETE'
    });

    req.then(function () {
        toastr.success(
            document.getElementById('changePpModal-opOk').textContent
        );
    }, function (xhr) {
        toastr.error(
            document.getElementById('changePpModal-opErr').textContent +
            " [SERVER] " +
            xhr.responseText
        );
    })
}

let changePpModalFile = document.getElementById("changePpModal-file");
changePpModalFile.addEventListener("change", ppModalListener_change);
document.getElementById("changePpModal").addEventListener("show.bs.modal", ppModalListener_show);
document.getElementById("changePpModal-save").addEventListener("click", ppModalListener_submit);
document.getElementById("changePpModal-delete").addEventListener("click", ppModalListener_delete);