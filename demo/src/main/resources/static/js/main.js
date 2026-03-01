/* ============================================
   MAIN.JS - Funcionalidades Globales
   ============================================ */

// Esperar a que el DOM esté completamente cargado
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

/* ============================================
   INICIALIZACIÓN
   ============================================ */
function initializeApp() {
    // Inicializar tooltips
    initTooltips();

    // Validación de formularios
    initFormValidation();

    // Animaciones de scroll
    initScrollAnimations();

    // Mobile menu
    initMobileMenu();

    // Lazy loading de imágenes
    initLazyLoading();

    // Configurar fecha mínima en inputs de fecha
    setMinDateInputs();

    console.log('TicketMaster App Initialized');
}

/* ============================================
   TOOLTIPS
   ============================================ */
function initTooltips() {
    const tooltipElements = document.querySelectorAll('[data-tooltip]');

    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', showTooltip);
        element.addEventListener('mouseleave', hideTooltip);
    });
}

function showTooltip(e) {
    const text = e.target.getAttribute('data-tooltip');
    const tooltip = document.createElement('div');
    tooltip.className = 'tooltip';
    tooltip.textContent = text;
    tooltip.id = 'tooltip-' + Date.now();

    document.body.appendChild(tooltip);

    const rect = e.target.getBoundingClientRect();
    tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = rect.top - tooltip.offsetHeight - 10 + 'px';

    setTimeout(() => tooltip.classList.add('show'), 10);
}

function hideTooltip() {
    const tooltips = document.querySelectorAll('.tooltip');
    tooltips.forEach(tooltip => {
        tooltip.classList.remove('show');
        setTimeout(() => tooltip.remove(), 300);
    });
}

/* ============================================
   VALIDACIÓN DE FORMULARIOS
   ============================================ */
function initFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
                showNotification('Por favor completa todos los campos requeridos', 'error');
            }
        });

        // Validación en tiempo real
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });
        });
    });
}

function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');

    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    return isValid;
}

function validateField(field) {
    const value = field.value.trim();
    let isValid = true;
    let errorMessage = '';

    // Campo requerido
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = 'Este campo es requerido';
    }

    // Email
    if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
            errorMessage = 'Email inválido';
        }
    }

    // Teléfono
    if (field.type === 'tel' && value) {
        const phoneRegex = /^[\d\s\-+()]+$/;
        if (!phoneRegex.test(value)) {
            isValid = false;
            errorMessage = 'Teléfono inválido';
        }
    }

    // Número de tarjeta
    if (field.name === 'numeroTarjeta' && value) {
        if (!validarNumeroTarjeta(value)) {
            isValid = false;
            errorMessage = 'Número de tarjeta inválido';
        }
    }

    // CVV
    if (field.name === 'cvv' && value) {
        if (!/^\d{3,4}$/.test(value)) {
            isValid = false;
            errorMessage = 'CVV inválido';
        }
    }

    // Pattern personalizado
    if (field.hasAttribute('pattern') && value) {
        const pattern = new RegExp(field.getAttribute('pattern'));
        if (!pattern.test(value)) {
            isValid = false;
            errorMessage = 'Formato inválido';
        }
    }

    // Mostrar/ocultar error
    showFieldError(field, isValid, errorMessage);

    return isValid;
}

function showFieldError(field, isValid, message) {
    // Remover error anterior
    const existingError = field.parentElement.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }

    if (!isValid) {
        field.classList.add('error');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'field-error';
        errorDiv.textContent = message;
        field.parentElement.appendChild(errorDiv);
    } else {
        field.classList.remove('error');
    }
}

/* ============================================
   VALIDACIÓN DE TARJETA (ALGORITMO DE LUHN)
   ============================================ */
function validarNumeroTarjeta(numero) {
    // Remover espacios
    numero = numero.replace(/\s/g, '');

    // Verificar que sean solo dígitos y longitud correcta
    if (!/^\d{13,19}$/.test(numero)) {
        return false;
    }

    // Algoritmo de Luhn
    let suma = 0;
    let doble = false;

    for (let i = numero.length - 1; i >= 0; i--) {
        let digito = parseInt(numero.charAt(i));

        if (doble) {
            digito *= 2;
            if (digito > 9) {
                digito -= 9;
            }
        }

        suma += digito;
        doble = !doble;
    }

    return (suma % 10) === 0;
}

function formatearNumeroTarjeta(input) {
    let value = input.value.replace(/\s/g, '');
    input.value = value.match(/.{1,4}/g)?.join(' ') || value;
}

function formatearTelefono(input) {
    let value = input.value.replace(/\D/g, '');

    if (value.length >= 10) {
        // Formato: (123) 456-7890
        value = value.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
    }

    input.value = value;
}

function formatearCodigoPostal(input) {
    let value = input.value.replace(/\D/g, '');
    input.value = value.substring(0, 5);
}

/* ============================================
   ANIMACIONES DE SCROLL
   ============================================ */
function initScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const animatedElements = document.querySelectorAll('.card, .category-card, .venue-card');
    animatedElements.forEach(el => observer.observe(el));
}

/* ============================================
   MENÚ MÓVIL
   ============================================ */
function initMobileMenu() {
    const menuToggle = document.createElement('button');
    menuToggle.className = 'mobile-menu-toggle';
    menuToggle.innerHTML = '☰';
    menuToggle.setAttribute('aria-label', 'Toggle Menu');

    const navbar = document.querySelector('.navbar .container');
    const navMenu = document.querySelector('.nav-menu');

    if (navbar && navMenu) {
        navbar.insertBefore(menuToggle, navMenu);

        menuToggle.addEventListener('click', function() {
            navMenu.classList.toggle('active');
            this.innerHTML = navMenu.classList.contains('active') ? '✕' : '☰';
        });

        // Cerrar menú al hacer click en un enlace
        navMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                navMenu.classList.remove('active');
                menuToggle.innerHTML = '☰';
            });
        });
    }
}

/* ============================================
   LAZY LOADING DE IMÁGENES
   ============================================ */
function initLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');

    const imageObserver = new IntersectionObserver(function(entries, observer) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.getAttribute('data-src');
                img.removeAttribute('data-src');
                img.classList.add('loaded');
                observer.unobserve(img);
            }
        });
    });

    images.forEach(img => imageObserver.observe(img));
}

/* ============================================
   NOTIFICACIONES
   ============================================ */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;

    const icon = {
        'success': '✓',
        'error': '✕',
        'warning': '⚠',
        'info': 'ℹ'
    }[type] || 'ℹ';

    notification.innerHTML = `
        <span class="notification-icon">${icon}</span>
        <span class="notification-message">${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">✕</button>
    `;

    document.body.appendChild(notification);

    setTimeout(() => notification.classList.add('show'), 10);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}

/* ============================================
   VALIDACIÓN DE FECHAS
   ============================================ */
function setMinDateInputs() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];

    dateInputs.forEach(input => {
        if (!input.hasAttribute('min')) {
            input.setAttribute('min', today);
        }
    });
}

function validarDiaLaboral(fecha) {
    const date = new Date(fecha + 'T00:00:00');
    const dia = date.getDay();

    // 0 = Domingo, 6 = Sábado
    return dia !== 0 && dia !== 6;
}

function esDiaFestivo(fecha) {
    // Lista de días festivos (ejemplo para México)
    const festivos = [
        '2026-01-01', // Año Nuevo
        '2026-02-02', // Día de la Constitución
        '2026-03-16', // Natalicio de Benito Juárez
        '2026-05-01', // Día del Trabajo
        '2026-09-16', // Día de la Independencia
        '2026-11-02', // Día de Muertos
        '2026-11-16', // Revolución Mexicana
        '2026-12-25'  // Navidad
    ];

    return festivos.includes(fecha);
}

/* ============================================
   CONTADOR DE CANTIDAD
   ============================================ */
function incrementQuantity(inputId = 'cantidad') {
    const input = document.getElementById(inputId);
    const max = parseInt(input.getAttribute('max')) || 10;
    const current = parseInt(input.value) || 0;

    if (current < max) {
        input.value = current + 1;
        input.dispatchEvent(new Event('change'));
    }
}

function decrementQuantity(inputId = 'cantidad') {
    const input = document.getElementById(inputId);
    const min = parseInt(input.getAttribute('min')) || 1;
    const current = parseInt(input.value) || 0;

    if (current > min) {
        input.value = current - 1;
        input.dispatchEvent(new Event('change'));
    }
}

/* ============================================
   LOADING SPINNER
   ============================================ */
function showLoading() {
    const loader = document.createElement('div');
    loader.className = 'loading-overlay';
    loader.id = 'globalLoader';
    loader.innerHTML = `
        <div class="spinner"></div>
        <p>Procesando...</p>
    `;
    document.body.appendChild(loader);
    setTimeout(() => loader.classList.add('show'), 10);
}

function hideLoading() {
    const loader = document.getElementById('globalLoader');
    if (loader) {
        loader.classList.remove('show');
        setTimeout(() => loader.remove(), 300);
    }
}

/* ============================================
   CONFIRMACIÓN DE ACCIONES
   ============================================ */
function confirmarAccion(mensaje, callback) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>Confirmar Acción</h3>
            <p>${mensaje}</p>
            <div class="modal-actions">
                <button class="btn btn-secondary" onclick="this.closest('.modal-overlay').remove()">
                    Cancelar
                </button>
                <button class="btn btn-primary" id="confirmButton">
                    Confirmar
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    document.getElementById('confirmButton').addEventListener('click', function() {
        modal.remove();
        callback();
    });

    // Cerrar al hacer click fuera
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

/* ============================================
   COPIAR AL PORTAPAPELES
   ============================================ */
function copiarAlPortapapeles(texto) {
    navigator.clipboard.writeText(texto).then(() => {
        showNotification('Copiado al portapapeles', 'success');
    }).catch(err => {
        console.error('Error al copiar:', err);
        showNotification('Error al copiar', 'error');
    });
}

/* ============================================
   FORMATEO DE MONEDA
   ============================================ */
function formatearMoneda(cantidad, moneda = 'MXN') {
    const formatos = {
        'MXN': { locale: 'es-MX', currency: 'MXN' },
        'USD': { locale: 'en-US', currency: 'USD' },
        'EUR': { locale: 'es-ES', currency: 'EUR' }
    };

    const formato = formatos[moneda] || formatos['MXN'];

    return new Intl.NumberFormat(formato.locale, {
        style: 'currency',
        currency: formato.currency
    }).format(cantidad);
}

/* ============================================
   DETECCIÓN DE TIPO DE TARJETA
   ============================================ */
function detectarTipoTarjeta(numero) {
    numero = numero.replace(/\s/g, '');

    const tipos = {
        'visa': /^4/,
        'mastercard': /^5[1-5]/,
        'amex': /^3[47]/,
        'discover': /^6(?:011|5)/,
        'dinersclub': /^3(?:0[0-5]|[68])/,
        'jcb': /^35/
    };

    for (let tipo in tipos) {
        if (tipos[tipo].test(numero)) {
            return tipo;
        }
    }

    return 'unknown';
}

/* ============================================
   SMOOTH SCROLL
   ============================================ */
function smoothScrollTo(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

/* ============================================
   FORMATEO DE FECHA
   ============================================ */
function formatearFecha(fecha, formato = 'largo') {
    const date = new Date(fecha);

    const formatos = {
        'corto': { day: '2-digit', month: '2-digit', year: 'numeric' },
        'medio': { day: 'numeric', month: 'short', year: 'numeric' },
        'largo': { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }
    };

    return date.toLocaleDateString('es-MX', formatos[formato] || formatos['largo']);
}

/* ============================================
   VALIDACIÓN DE EDAD
   ============================================ */
function validarEdad(fechaNacimiento, edadMinima = 18) {
    const hoy = new Date();
    const nacimiento = new Date(fechaNacimiento);
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();

    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
        edad--;
    }

    return edad >= edadMinima;
}

/* ============================================
   TIMER DE CUENTA REGRESIVA
   ============================================ */
function iniciarCuentaRegresiva(elementId, minutos) {
    const element = document.getElementById(elementId);
    if (!element) return;

    let segundosRestantes = minutos * 60;

    const interval = setInterval(() => {
        const mins = Math.floor(segundosRestantes / 60);
        const secs = segundosRestantes % 60;

        element.textContent = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

        if (segundosRestantes <= 0) {
            clearInterval(interval);
            element.textContent = 'Tiempo expirado';
            // Aquí puedes agregar lógica adicional cuando expire el tiempo
        }

        segundosRestantes--;
    }, 1000);
}

/* ============================================
   DEBOUNCE (OPTIMIZACIÓN)
   ============================================ */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/* ============================================
   LOCAL STORAGE HELPERS
   ============================================ */
const Storage = {
    set: function(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (e) {
            console.error('Error al guardar en localStorage:', e);
            return false;
        }
    },

    get: function(key) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (e) {
            console.error('Error al leer de localStorage:', e);
            return null;
        }
    },

    remove: function(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (e) {
            console.error('Error al eliminar de localStorage:', e);
            return false;
        }
    },

    clear: function() {
        try {
            localStorage.clear();
            return true;
        } catch (e) {
            console.error('Error al limpiar localStorage:', e);
            return false;
        }
    }
};

/* ============================================
   EXPORTAR FUNCIONES GLOBALES
   ============================================ */
window.TicketMaster = {
    showNotification,
    showLoading,
    hideLoading,
    confirmarAccion,
    copiarAlPortapapeles,
    formatearMoneda,
    formatearFecha,
    incrementQuantity,
    decrementQuantity,
    validarNumeroTarjeta,
    detectarTipoTarjeta,
    validarEdad,
    smoothScrollTo,
    Storage
};

console.log('TicketMaster Utils loaded successfully');