// --- ENUMS ---
export type ProgressStatus = 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'CHANGES_REQUESTED';
export type UserRole = 'STUDENT' | 'TUTOR' | 'ADMIN';

// --- USER DTOs ---
export interface UserCreateRequest {
  cognitoId: string;
  email: string;
  fullName: string;
  role: UserRole;
}

export interface UserResponse {
  id: number;
  cognitoId: string;
  email: string;
  fullName: string;
  role: UserRole;
  createdAt: string;
}

// --- THESIS DTOs ---
export interface ThesisCreateRequest {
  title: string;
  description?: string;
  repositoryUrl: string;
}

export interface ThesisResponse {
  id: number;
  title: string;
  description?: string;
  repositoryUrl: string;
  studentCognitoId: string;
  tutorCognitoId?: string;
  status: string;
  createdAt: string;
}

export interface SubmissionStatusResponse {
  id: number;
  submissionId: number;
  status: ProgressStatus;
  updatedAt: string;
}

export interface ReviewResponse {
  id: number;
  submissionId: number;
  comment: string;
  reviewedAt: string;
}

export interface SubmissionHistoryResponse {
  submissionId: number;
  commitUrl: string;
  previousSubmissionId?: number;
  uploadedAt: string;
  currentStatus?: ProgressStatus;
  approvedAt?: string;
  statusHistory: SubmissionStatusResponse[];
  reviews: ReviewResponse[];
}

export interface ThesisHistoryResponse {
  thesisId: number;
  title: string;
  description?: string;
  repositoryUrl: string;
  studentCognitoId: string;
  tutorCognitoId?: string;
  status: string;
  createdAt: string;
  submissions: SubmissionHistoryResponse[];
}

// --- SUBMISSION DTOs ---
export interface SubmissionCreateRequest {
  thesisId: number;
  commitUrl: string;
  previousSubmissionId?: number;
}

export interface SubmissionResponse {
  id: number;
  thesisId: number;
  commitUrl: string;
  previousSubmissionId?: number;
  currentStatus?: ProgressStatus;
  uploadedAt?: string;
}

// --- REVIEW DTOs ---
export interface ReviewCreateRequest {
  thesisId: number;
  comment: string;
}

export interface StatusUpdateRequest {
  status: ProgressStatus;
}