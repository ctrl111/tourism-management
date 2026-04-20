/**
 * Утилита для работы со статусами
 * Преобразует статусы из базы данных в локализованные строки
 */

import { useI18n } from 'vue-i18n'

/**
 * Получить локализованное название статуса
 * @param {string} status - Статус из базы данных (ACTIVE, INACTIVE, и т.д.)
 * @returns {string} - Локализованное название статуса
 */
export function getStatusLabel(status) {
  const { t } = useI18n()
  return t(`status.${status}`) || status
}

/**
 * Получить тип тега для статуса
 * @param {string} status - Статус
 * @returns {string} - Тип тега Element Plus (success, danger, warning, info)
 */
export function getStatusTagType(status) {
  const statusTypeMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'danger',
    'ENABLED': 'success',
    'DISABLED': 'danger',
    'PENDING': 'warning',
    'PAID': 'success',
    'CANCELLED': 'info',
    'REFUNDED': 'danger',
    'COMPLETED': 'success',
    'PROCESSING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'PUBLISHED': 'success',
    'DRAFT': 'info',
    'DELETED': 'danger',
    'NORMAL': 'success',
    'BLOCKED': 'danger',
    'YES': 'success',
    'NO': 'danger',
    'READ': 'success',
    'UNREAD': 'warning',
    'ON_SALE': 'success',
    'OFF_SALE': 'info'
  }
  
  return statusTypeMap[status] || 'info'
}

/**
 * Список статусов пользователя
 */
export const userStatusOptions = [
  { label: 'status.ACTIVE', value: 'ACTIVE' },
  { label: 'status.INACTIVE', value: 'INACTIVE' }
]

/**
 * Список статусов заказа
 */
export const orderStatusOptions = [
  { label: 'status.PENDING', value: 'PENDING' },
  { label: 'status.PAID', value: 'PAID' },
  { label: 'status.CANCELLED', value: 'CANCELLED' },
  { label: 'status.REFUNDED', value: 'REFUNDED' }
]

/**
 * Список статусов достопримечательности
 */
export const scenicStatusOptions = [
  { label: 'status.ON_SALE', value: 'ACTIVE' },
  { label: 'status.OFF_SALE', value: 'INACTIVE' }
]
