import { axiosClient } from '../api/axiosClient';
import type {
  SubmissionCreateRequest,
  SubmissionResponse,
  ReviewCreateRequest,
  ReviewResponse,
  StatusUpdateRequest,
  SubmissionStatusResponse,
} from '../types/index';

export const submissionService = {
  createSubmission: async (data: SubmissionCreateRequest): Promise<SubmissionResponse> => {
    const response = await axiosClient.post<SubmissionResponse>('/api/docutesis/submissions', data);
    return response.data;
  },

  getPendingSubmissions: async (): Promise<SubmissionResponse[]> => {
    const response = await axiosClient.get<SubmissionResponse[]>('/api/docutesis/submissions/pending');
    return response.data;
  },

  getApprovedSubmissions: async (): Promise<SubmissionResponse[]> => {
    const response = await axiosClient.get<SubmissionResponse[]>('/api/docutesis/submissions/approved');
    return response.data;
  },

  deleteSubmission: async (id: number): Promise<void> => {
    await axiosClient.delete(`/api/docutesis/submissions/${id}`);
  },

  addReview: async (submissionId: number, data: ReviewCreateRequest): Promise<ReviewResponse> => {
    const response = await axiosClient.post<ReviewResponse>(
      `/api/docutesis/submissions/${submissionId}/reviews`,
      data
    );
    return response.data;
  },

  updateStatus: async (id: number, data: StatusUpdateRequest): Promise<SubmissionStatusResponse> => {
    const response = await axiosClient.patch<SubmissionStatusResponse>(
      `/api/docutesis/submissions/${id}/status`,
      data
    );
    return response.data;
  },

  getReviews: async (submissionId: number): Promise<ReviewResponse[]> => {
    const response = await axiosClient.get<ReviewResponse[]>(
      `/api/docutesis/submissions/${submissionId}/reviews`
    );
    return response.data;
  },
};