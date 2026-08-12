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
  IonCardContent,
  IonButton,
  IonItem,
  IonLabel,
  IonInput,
  IonTextarea,
  IonList,
  IonBadge,
  IonSpinner,
  IonToast,
  IonButtons,
} from '@ionic/react';
import { thesisService } from '../services/thesisService';
import { submissionService } from '../services/submissionService';
import type { ThesisResponse, ThesisHistoryResponse } from '../types';
import { useAuth } from '../context/AuthContext';

export const StudentDashboard: React.FC = () => {
  const { logout } = useAuth();
  const [thesis, setThesis] = useState<ThesisResponse | null>(null);
  const [history, setHistory] = useState<ThesisHistoryResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // Campos formulario Tesis
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [repositoryUrl, setRepositoryUrl] = useState('');

  // Campos formulario Entregas (Submissions)
  const [commitUrl, setCommitUrl] = useState('');

  const [toastMsg, setToastMsg] = useState('');

  const loadStudentData = async () => {
    setLoading(true);
    try {
      const data = await thesisService.getMyThesis();
      setThesis(data);
      const historyData = await thesisService.getThesisHistory(data.id);
      setHistory(historyData);
    } catch (err) {
      // Si falla (ej. 404), es porque aún no tiene una tesis registrada
      setThesis(null);
      setHistory(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudentData();
  }, []);

  const handleCreateThesis = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await thesisService.createThesis({ title, description, repositoryUrl });
      setToastMsg('Tesis registrada con éxito');
      loadStudentData();
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Error al crear la tesis');
    }
  };

  const handleCreateSubmission = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!thesis) return;

    // Enlazar automáticamente con el ID de la última entrega realizada (si existe)
    const lastSubmissionId =
      history?.submissions && history.submissions.length > 0
        ? history.submissions[history.submissions.length - 1].submissionId
        : undefined;

    try {
      await submissionService.createSubmission({
        thesisId: thesis.id,
        commitUrl,
        previousSubmissionId: lastSubmissionId,
      });
      setToastMsg('Entrega registrada correctamente');
      setCommitUrl('');
      loadStudentData();
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Error al registrar la entrega');
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="primary">
          <IonTitle>Panel del Estudiante</IonTitle>
          <IonButtons slot="end">
            <IonButton onClick={logout}>Salir</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>

      <IonContent className="ion-padding">
        {loading ? (
          <IonSpinner name="crescent" />
        ) : !thesis ? (
          /* Formulario si no posee tesis registrada */
          <IonCard>
            <IonCardHeader>
              <IonCardTitle>Registrar Nueva Tesis</IonCardTitle>
            </IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleCreateThesis}>
                <IonItem lines="full">
                  <IonLabel position="stacked">Título</IonLabel>
                  <IonInput
                    placeholder="Ingrese el título de su tesis"
                    value={title}
                    onIonInput={(e: CustomEvent) => setTitle((e.detail.value as string) || '')}
                    required
                  />
                </IonItem>
                <IonItem lines="full" className="ion-margin-top">
                  <IonLabel position="stacked">Descripción</IonLabel>
                  <IonTextarea
                    placeholder="Breve resumen del proyecto de tesis"
                    value={description}
                    onIonInput={(e: CustomEvent) => setDescription((e.detail.value as string) || '')}
                  />
                </IonItem>
                <IonItem lines="full" className="ion-margin-top">
                  <IonLabel position="stacked">URL del Repositorio</IonLabel>
                  <IonInput
                    placeholder="https://github.com/usuario/repositorio"
                    value={repositoryUrl}
                    onIonInput={(e: CustomEvent) => setRepositoryUrl((e.detail.value as string) || '')}
                    required
                  />
                </IonItem>
                <IonButton expand="block" type="submit" className="ion-margin-top">
                  Guardar Tesis
                </IonButton>
              </form>
            </IonCardContent>
          </IonCard>
        ) : (
          /* Vista de la tesis e historial de entregas */
          <>
            <IonCard>
              <IonCardHeader>
                <IonCardTitle>{thesis.title}</IonCardTitle>
              </IonCardHeader>
              <IonCardContent>
                <p>
                  <strong>Estado:</strong>{' '}
                  <IonBadge color={thesis.status === 'APPROVED' ? 'success' : 'warning'}>
                    {thesis.status}
                  </IonBadge>
                </p>
                <p>
                  <strong>Repositorio:</strong>{' '}
                  <a href={thesis.repositoryUrl} target="_blank" rel="noreferrer">
                    {thesis.repositoryUrl}
                  </a>
                </p>
                <p>{thesis.description}</p>
              </IonCardContent>
            </IonCard>

            {thesis.status !== 'APPROVED' && (
              <IonCard>
                <IonCardHeader>
                  <IonCardTitle>Nueva Entrega (Submission)</IonCardTitle>
                </IonCardHeader>
                <IonCardContent>
                  <form onSubmit={handleCreateSubmission}>
                    <IonItem lines="full">
                      <IonLabel position="stacked">URL del Commit</IonLabel>
                      <IonInput
                        placeholder="https://github.com/usuario/repo/commit/hash"
                        value={commitUrl}
                        onIonInput={(e: CustomEvent) => setCommitUrl((e.detail.value as string) || '')}
                        required
                      />
                    </IonItem>
                    <IonButton
                      expand="block"
                      type="submit"
                      color="secondary"
                      className="ion-margin-top"
                    >
                      Enviar Commit
                    </IonButton>
                  </form>
                </IonCardContent>
              </IonCard>
            )}

            <h3>Historial de Entregas</h3>
            <IonList>
              {history?.submissions.map((sub) => (
                <IonCard key={sub.submissionId}>
                  <IonCardContent>
                    <p>
                      <strong>Commit:</strong>{' '}
                      <a href={sub.commitUrl} target="_blank" rel="noreferrer">
                        {sub.commitUrl}
                      </a>
                    </p>
                    <p>
                      <strong>Estado actual:</strong>{' '}
                      <IonBadge
                        color={
                          sub.currentStatus === 'APPROVED'
                            ? 'success'
                            : sub.currentStatus === 'CHANGES_REQUESTED'
                            ? 'danger'
                            : 'warning'
                        }
                      >
                        {sub.currentStatus || 'PENDING'}
                      </IonBadge>
                    </p>

                    {sub.reviews.length > 0 && (
                      <div>
                        <p>
                          <strong>Revisiones del Tutor:</strong>
                        </p>
                        {sub.reviews.map((rev) => (
                          <blockquote key={rev.id}>- {rev.comment}</blockquote>
                        ))}
                      </div>
                    )}
                  </IonCardContent>
                </IonCard>
              ))}
            </IonList>
          </>
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