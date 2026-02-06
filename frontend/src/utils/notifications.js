/**
 * Toast Notification System
 * 
 * Provides simple toast notifications to replace Quasar's $q.notify()
 */

let toastContainer = null

/**
 * Initialize the toast container
 */
function initToastContainer() {
    if (!toastContainer) {
        toastContainer = document.createElement('div')
        toastContainer.className = 'toast-container'
        document.body.appendChild(toastContainer)
    }
    return toastContainer
}

/**
 * Show a toast notification
 * @param {Object} options - Notification options
 * @param {string} options.type - Type: 'success', 'error', 'warning', 'info', 'positive', 'negative'
 * @param {string} options.message - Message to display
 * @param {string} [options.icon] - Optional icon name
 * @param {number} [options.timeout=3000] - Duration in ms
 */
export function showNotification({ type, message, icon, timeout = 3000 }) {
    const container = initToastContainer()

    // Normalize type names (Quasar uses 'positive'/'negative')
    const normalizedType = type === 'positive' ? 'success' : type === 'negative' ? 'error' : type

    // Create toast element
    const toast = document.createElement('div')
    toast.className = `toast ${normalizedType}`

    // Default icons based on type
    const defaultIcons = {
        success: 'check_circle',
        error: 'error',
        warning: 'warning',
        info: 'info'
    }

    const iconName = icon || defaultIcons[normalizedType] || 'notifications'

    toast.innerHTML = `
    <span class="material-icons toast-icon">${iconName}</span>
    <span class="toast-message">${message}</span>
  `

    container.appendChild(toast)

    // Auto-remove after timeout
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease'
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast)
            }
        }, 300)
    }, timeout)
}

// Add slideOut animation
if (typeof document !== 'undefined') {
    const style = document.createElement('style')
    style.textContent = `
    @keyframes slideOut {
      to {
        transform: translateX(100%);
        opacity: 0;
      }
    }
  `
    document.head.appendChild(style)
}
