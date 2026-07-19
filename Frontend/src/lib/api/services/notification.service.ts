import { apiClient } from "../client";

export interface Notification {
  id: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  type: string;
}

export interface NotificationPage {
  content: Notification[];
  totalElements: number;
  totalPages: number;
}

export const NotificationService = {
  getNotifications: async (page = 0, size = 20): Promise<NotificationPage> => {
    const { data } = await apiClient.get(`/notifications?page=${page}&size=${size}`);
    return data.data;
  },
  
  markAsRead: async (id: string): Promise<void> => {
    await apiClient.patch(`/notifications/${id}/read`);
  }
};
