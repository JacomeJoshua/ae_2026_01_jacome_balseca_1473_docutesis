import React, { useEffect, useState } from 'react';
import {
  IonContent,
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonButton,
  IonItem,
  IonLabel,
  IonTextarea,
  IonSelect,
  IonSelectOption,
  IonList,
  IonSpinner,
  IonToast,
  IonButtons,
  IonBadge,
} from '@ionic/react';
import { submissionService } from '../services/submissionService';
import { reviewService } from '../services/reviewService';
import { thesisService } from '../services/thesisService';
import type { SubmissionResponse, ProgressStatus, ThesisResponse } from '../types';
import { useAuth } from '../context/AuthContext';

export const TutorDashboard: React.FC = () => {
  const { logout } = useAuth();
  const [assignedTheses, setAssignedTheses] = useState<ThesisResponse[]>([]);
  const [pendingSubmissions, setPendingSubmissions] = useState<SubmissionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [toastMsg, setToastMsg] = useState('');

  // Estado para el formulario de revisión
  const [selectedSub, setSelectedSub] = useState<SubmissionResponse | null>(null);
  const [reviewComment, setReviewComment] = useState('');
  const [selectedStatus, setSelectedStatus] = useState<ProgressStatus>('UNDER_REVIEW');

  const loadData = async () => {
    setLoading(true);
    try {
      const [thesesData, submissionsData] = await Promise.all([
        thesisService.getMyAssignedTheses(),
        submissionService.getPendingSubmissions(),
      ]);
      setAssignedTheses(thesesData);

      // Filtrar entregas pendientes solo para las tesis asignadas a este tutor
      const assignedThesisIds = new Set(thesesData.map((t) => t.id));
      const filteredSubmissions = submissionsData.filter((sub) =>
        assignedThesisIds.has(sub.thesisId)
      );
      setPendingSubmissions(filteredSubmissions);
    } catch (err: any) {
      setToastMsg('Error al cargar la información del tutor');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleAddReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedSub) return;
    try {
      await reviewService.addReview(selectedSub.id, {
        thesisId: selectedSub.thesisId,
        comment: reviewComment,
      });

      await reviewService.updateStatus(selectedSub.id, {
        status: selectedStatus,
      });

      setToastMsg('Revisión y estado guardados correctamente');
      setSelectedSub(null);
      setReviewComment('');
      loadData();
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Error al guardar revisión');
    }
  };

  const handleApproveThesis = async (thesisId: number) => {
    try {
      await thesisService.approveThesis(thesisId);
      setToastMsg('Tesis aprobada exitosamente');
      loadData();
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Error al aprobar la tesis');
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="tertiary">
          <IonTitle>Panel del Tutor</IonTitle>
          <IonButtons slot="end">
            <IonButton onClick={logout}>Salir</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>

      <IonContent className="ion-padding">
        <h2>Mis Tesis Asignadas</h2>

        {loading ? (
          <IonSpinner name="crescent" />
        ) : assignedTheses.length === 0 ? (
          <p>No tienes tesis asignadas actualmente.</p>
        ) : (
          <IonList>
            {assignedTheses.map((thesis) => (
              <IonCard key={thesis.id} color="light">
                <IonCardHeader>
                  <IonCardTitle>{thesis.title}</IonCardTitle>
                  <IonCardSubtitle>
                    Estado Global:{' '}
                    <IonBadge color={thesis.status === 'APPROVED' ? 'success' : 'warning'}>
                      {thesis.status}
                    </IonBadge>
                  </IonCardSubtitle>
                </IonCardHeader>
                <IonCardContent>
                  <p>{thesis.description || 'Sin descripción'}</p>
                  <p>
                    <strong>Repositorio:</strong>{' '}
                    <a href={thesis.repositoryUrl} target="_blank" rel="noreferrer">
                      {thesis.repositoryUrl}
                    </a>
                  </p>
                  <p>
                    <strong>Estudiante ID:</strong> {thesis.studentCognitoId}
                  </p>

                  {thesis.status !== 'APPROVED' && (
                    <IonButton
                      size="small"
                      color="success"
                      className="ion-margin-top"
                      onClick={() => handleApproveThesis(thesis.id)}
                    >
                      Aprobar Tesis Global
                    </IonButton>
                  )}
                </IonCardContent>
              </IonCard>
            ))}
          </IonList>
        )}

        <h2 className="ion-margin-top">Entregas Pendientes de Revisión</h2>

        {loading ? (
          <IonSpinner name="crescent" />
        ) : pendingSubmissions.length === 0 ? (
          <p>No hay entregas pendientes por revisar para tus tesis asignadas.</p>
        ) : (
          <IonList>
            {pendingSubmissions.map((sub) => (
              <IonCard key={sub.id}>
                <IonCardHeader>
                  <IonCardTitle>Tesis ID: {sub.thesisId}</IonCardTitle>
                </IonCardHeader>
                <IonCardContent>
                  <p>
                    <strong>Commit URL:</strong>{' '}
                    <a href={sub.commitUrl} target="_blank" rel="noreferrer">
                      {sub.commitUrl}
                    </a>
                  </p>
                  <p>
                    <strong>Fecha:</strong> {sub.uploadedAt}
                  </p>

                  <IonButton
                    size="small"
                    color="primary"
                    className="ion-margin-top"
                    onClick={() => {
                      setSelectedSub(sub);
                      setReviewComment('');
                    }}
                  >
                    Evaluar Commit
                  </IonButton>
                </IonCardContent>
              </IonCard>
            ))}
          </IonList>
        )}

        {/* Formulario de evaluación rápida */}
        {selectedSub && (
          <IonCard color="light" className="ion-margin-top">
            <IonCardHeader>
              <IonCardTitle>Revisar Entrega #{selectedSub.id}</IonCardTitle>
            </IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleAddReview}>
                <IonItem lines="full">
                  <IonLabel position="stacked">Observación / Comentario</IonLabel>
                  <IonTextarea
                    placeholder="Escribe tus observaciones aquí..."
                    value={reviewComment}
                    onIonChange={(e) => setReviewComment(e.detail.value!)}
                    required
                    rows={4}
                  />
                </IonItem>

                <IonItem lines="full" className="ion-margin-top">
                  <IonLabel position="stacked">Nuevo Estado</IonLabel>
                  <IonSelect
                    value={selectedStatus}
                    onIonChange={(e) => setSelectedStatus(e.detail.value)}
                    interface="popover"
                  >
                    <IonSelectOption value="UNDER_REVIEW">En Revisión</IonSelectOption>
                    <IonSelectOption value="CHANGES_REQUESTED">Solicitar Cambios</IonSelectOption>
                    <IonSelectOption value="APPROVED">Aprobado</IonSelectOption>
                  </IonSelect>
                </IonItem>

                <IonButton expand="block" type="submit" color="tertiary" className="ion-margin-top">
                  Enviar Retroalimentación
                </IonButton>
                <IonButton
                  expand="block"
                  fill="outline"
                  color="medium"
                  onClick={() => {
                    setSelectedSub(null);
                    setReviewComment('');
                  }}
                >
                  Cancelar
                </IonButton>
              </form>
            </IonCardContent>
          </IonCard>
        )}

        <IonToast
          isOpen={!!toastMsg}
          onDidDismiss={() => setToastMsg('')}
          message={toastMsg}
          duration={3000}
        />
      </IonContent>
    </IonPage>
  );
};