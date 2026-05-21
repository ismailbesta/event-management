export interface IEvents {
  content: Event[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  pageable: Pageable;
  size: number;
  sort: Sort;
  totalElements: number;
  totalPages: number;
}

export interface Pageable {
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  sort: Sort;
  unpaged: boolean;
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Event {
  category: string;
  description: string;
  endDate: string;
  endTime: string;
  id: number;
  location: string;
  name: string;
  ownerFullName: string;
  participantCount: number;
  startDate: string;
  startTime: string;
  status: string;
}

export const EVENT_CATEGORY_LABELS: Record<string, string> = {
  GENERAL: 'Genel',
  TECHNOLOGY_AND_SOFTWARE: 'Teknoloji ve Yazılım',
  MUSIC_AND_CONCERTS: 'Müzik ve Konserler',
  ARTS_AND_CULTURE: 'Sanat ve Kültür',
  FOOD_AND_DRINK: 'Yemek ve İçecek',
  GAMING_AND_ESPORTS: 'Oyun ve E-Spor',
  SPORTS_AND_OUTDOORS: 'Spor ve Açık Hava',
  EDUCATION_AND_SEMINARS: 'Eğitim ve Seminerler',
  SOCIAL_RESPONSIBILITY: 'Sosyal Sorumluluk',
};

export const getEventCategoryLabel = (category: string) =>
  EVENT_CATEGORY_LABELS[category] ?? category;

export const EVENT_STATUS_LABELS: Record<string, string> = {
  UNPUBLISHED: 'Yayınlanmadı',
  PUBLISHED: 'Yayında',
  ONGOING: 'Başladı',
  COMPLETED: 'Tamamlandı',
  CANCELED: 'İptal edildi',
  ARCHIVED: 'Arşivlendi',
  TIMEOUT: 'Süresi Doldu',
};

export const getEventStatusLabel = (status: string) => EVENT_STATUS_LABELS[status] ?? status;
