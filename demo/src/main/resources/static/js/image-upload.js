/* ============================================
   IMAGE-UPLOAD.JS - Integración con API de Imágenes
   ============================================ */

document.addEventListener('DOMContentLoaded', function () {
    initImageUpload();
});

function initImageUpload() {
    const fileInput = document.getElementById('imagenEvento');
    const uploadBtn = document.getElementById('uploadImageBtn');
    const preview = document.getElementById('imagePreview');
    const hiddenInput = document.getElementById('imageId');
    const statusText = document.getElementById('uploadStatus');
    const listBtn = document.getElementById('listImagesBtn');
    const imageList = document.getElementById('imageList');

    if (!fileInput || !uploadBtn) return;

    // Upload image on button click
    uploadBtn.addEventListener('click', function () {
        const file = fileInput.files[0];
        if (!file) {
            showUploadStatus('Por favor selecciona un archivo primero.', 'error');
            return;
        }
        uploadImage(file);
    });

    // Also allow upload on file change (auto-upload)
    fileInput.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            // Show local preview immediately
            const reader = new FileReader();
            reader.onload = function (e) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });

    // List existing images
    if (listBtn) {
        listBtn.addEventListener('click', function () {
            fetchImageList();
        });
    }
}

/**
 * Upload a file to the Image API
 */
function uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);

    const hiddenInput = document.getElementById('imageId');
    const preview = document.getElementById('imagePreview');

    showUploadStatus('Subiendo imagen...', 'info');

    fetch(IMAGE_API_URL + '/api/images/upload', {
        method: 'POST',
        body: formData
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Error al subir la imagen: ' + response.status);
            }
            return response.text();
        })
        .then(function (imageId) {
            // Save the returned imageId into the hidden field
            hiddenInput.value = imageId;

            // Update preview to point to the API
            preview.src = IMAGE_API_URL + '/api/images/' + imageId;
            preview.style.display = 'block';

            showUploadStatus('Imagen subida exitosamente. ID: ' + imageId, 'success');
            console.log('Image uploaded, id:', imageId);
        })
        .catch(function (error) {
            console.error('Upload error:', error);
            showUploadStatus('Error: ' + error.message, 'error');
        });
}

/**
 * Fetch and display list of all images from the API
 */
function fetchImageList() {
    const imageList = document.getElementById('imageList');
    if (!imageList) return;

    imageList.innerHTML = '<p>Cargando...</p>';

    fetch(IMAGE_API_URL + '/api/images')
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Error al obtener imágenes: ' + response.status);
            }
            return response.json();
        })
        .then(function (images) {
            if (images.length === 0) {
                imageList.innerHTML = '<p>No hay imágenes disponibles.</p>';
                return;
            }

            var html = '<ul class="image-list-items">';
            images.forEach(function (img) {
                html += '<li class="image-list-item" data-id="' + img.id + '">';
                html += '<img src="' + IMAGE_API_URL + '/api/images/' + img.id + '" alt="' + img.name + '" class="image-thumbnail">';
                html += '<span>' + img.name + '</span>';
                html += '<button type="button" class="select-image-btn" onclick="selectExistingImage(\'' + img.id + '\', \'' + img.name + '\')">Seleccionar</button>';
                html += '</li>';
            });
            html += '</ul>';
            imageList.innerHTML = html;
        })
        .catch(function (error) {
            console.error('List error:', error);
            imageList.innerHTML = '<p style="color:red;">Error: ' + error.message + '</p>';
        });
}

/**
 * Select an existing image from the list
 */
function selectExistingImage(id, name) {
    var hiddenInput = document.getElementById('imageId');
    var preview = document.getElementById('imagePreview');

    hiddenInput.value = id;
    preview.src = IMAGE_API_URL + '/api/images/' + id;
    preview.style.display = 'block';

    showUploadStatus('Imagen seleccionada: ' + name + ' (ID: ' + id + ')', 'success');
}

/**
 * Show upload status message
 */
function showUploadStatus(message, type) {
    var statusText = document.getElementById('uploadStatus');
    if (!statusText) return;

    statusText.textContent = message;
    statusText.className = 'upload-status upload-status-' + type;
    statusText.style.display = 'block';
}

