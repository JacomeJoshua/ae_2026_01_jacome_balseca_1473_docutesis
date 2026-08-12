import { axiosClient } from '../api/axiosClient';
import type { UserCreateRequest, UserResponse } from '../types/index';

export const userService = {
  createUserProfile: async (data: UserCreateRequest): Promise<UserResponse> => {
    const response = await axiosClient.post<UserResponse>('/api/users/', data);
    return response.data;
  },

  getMyProfile: async (): Promise<UserResponse> => {
    const response = await axiosClient.get<UserResponse>('/api/users/me');
    return response.data;
  },

  getUserProfile: async (cognitoId: string): Promise<UserResponse> => {
    const response = await axiosClient.get<UserResponse>(`/api/users/${cognitoId}`);
    return response.data;
  },
};