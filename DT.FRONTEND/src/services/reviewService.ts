import api from './api';
import type { 
  ReviewCreateRequest, 
  ReviewResponse, 
  StatusUpdateRequest, 
  SubmissionStatusResponse 
} from '../types';

export const reviewService = {
  /**
   * Agrega un comentario de revisión a un commit/entrega.
   * POST /submissions/{submissionId}/reviews
   */
  async addReview(submissionId: number, data: ReviewCreateRequest): Promise<ReviewResponse> {
    const response = await api.post<ReviewResponse>(`/submissions/${submissionId}/reviews`, data);
    return response.data;
  },

  /**
   * Actualiza el estado de la entrega.
   * PATCH /submissions/{id}/status
   */
  async updateStatus(submissionId: number, data: StatusUpdateRequest): Promise<SubmissionStatusResponse> {
    const response = await api.patch<SubmissionStatusResponse>(`/submissions/${submissionId}/status`, data);
    return response.data;
  },

  /**
   * Obtiene la lista de revisiones de una entrega.
   * GET /submissions/{submissionId}/reviews
   */
  async getReviewsBySubmission(submissionId: number): Promise<ReviewResponse[]> {
    const response = await api.get<ReviewResponse[]>(`/submissions/${submissionId}/reviews`);
    return response.data;
  },
};