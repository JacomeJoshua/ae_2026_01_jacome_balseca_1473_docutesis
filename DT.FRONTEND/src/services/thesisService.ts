import { axiosClient } from '../api/axiosClient';
import type { ThesisCreateRequest, ThesisResponse, ThesisHistoryResponse } from '../types';

export const thesisService = {
  // Crear una nueva tesis (solo Rol STUDENT)
  createThesis: async (data: ThesisCreateRequest): Promise<ThesisResponse> => {
    const response = await axiosClient.post<ThesisResponse>('/api/docutesis/theses', data);
    return response.data;
  },

  // Obtener la tesis del estudiante autenticado
  getMyThesis: async (): Promise<ThesisResponse> => {
    const response = await axiosClient.get<ThesisResponse>('/api/docutesis/theses/me');
    return response.data;
  },

  // Obtener las tesis asignadas al tutor autenticado (solo Rol TUTOR)
  getMyAssignedTheses: async (): Promise<ThesisResponse[]> => {
    const response = await axiosClient.get<ThesisResponse[]>('/api/docutesis/theses/tutor/me');
    return response.data;
  },

  // Obtener todo el historial de entregas y revisiones de una tesis
  getThesisHistory: async (thesisId: number): Promise<ThesisHistoryResponse> => {
    const response = await axiosClient.get<ThesisHistoryResponse>(`/api/docutesis/theses/${thesisId}/history`);
    return response.data;
  },

  // Asignar tutor a la tesis (solo Rol ADMIN)
  assignTutor: async (thesisId: number, tutorCognitoId: string): Promise<ThesisResponse> => {
    const response = await axiosClient.put<ThesisResponse>(
      `/api/docutesis/theses/${thesisId}/assign-tutor`,
      null,
      { params: { tutorCognitoId } }
    );
    return response.data;
  },

  // Aprobar la tesis (solo Rol TUTOR)
  approveThesis: async (thesisId: number): Promise<ThesisResponse> => {
    const response = await axiosClient.put<ThesisResponse>(`/api/docutesis/theses/${thesisId}/approve`);
    return response.data;
  }
};