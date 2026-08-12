import React, { useState } from 'react';
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
  IonToast,
  IonButtons,
  IonBadge,
} from '@ionic/react';
import { thesisService } from '../services/thesisService';
import type { ThesisHistoryResponse } from '../types';
import { useAuth } from '../context/AuthContext';

export const AdminDashboard: React.FC = () => {
  const { logout } = useAuth();

  // Asignar Tutor
  const [thesisId, setThesisId] = useState('');
  const [tutorCognitoId, setTutorCognitoId] = useState('');

  // Consultar Historial
  const [searchThesisId, setSearchThesisId] = useState('');
  const [thesisHistory, setThesisHistory] = useState<ThesisHistoryResponse | null>(null);

  const [toastMsg, setToastMsg] = useState('');

  const handleAssignTutor = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await thesisService.assignTutor(Number(thesisId), tutorCognitoId);
      setToastMsg(`Tutor ${tutorCognitoId} asignado con éxito a la Tesis #${thesisId}`);
      setThesisId('');
      setTutorCognitoId('');
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Error al asignar tutor');
    }
  };

  const handleSearchHistory = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const data = await thesisService.getThesisHistory(Number(searchThesisId));
      setThesisHistory(data);
    } catch (err: any) {
      setToastMsg(err.response?.data?.message || 'Tesis no encontrada');
      setThesisHistory(null);
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="dark">
          <IonTitle>Panel de Administración</IonTitle>
          <IonButtons slot="end">
            <IonButton onClick={logout}>Salir</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>

      <IonContent className="ion-padding">
        {/* Sección: Asignar Tutor */}
        <IonCard>
          <IonCardHeader>
            <IonCardTitle>Asignar Tutor a Tesis</IonCardTitle>
          </IonCardHeader>
          <IonCardContent>
            <form onSubmit={handleAssignTutor}>
              <IonItem lines="full">
                <IonLabel position="stacked">ID de Tesis</IonLabel>
                <IonInput
                  type="number"
                  placeholder="Ingrese el ID (ej: 1)"
                  value={thesisId}
                  onIonChange={(e) => setThesisId(e.detail.value!)}
                  required
                />
              </IonItem>
              <IonItem lines="full" className="ion-margin-top">
                <IonLabel position="stacked">Cognito ID del Tutor (sub)</IonLabel>
                <IonInput
                  placeholder="Ingrese el UUID del tutor"
                  value={tutorCognitoId}
                  onIonChange={(e) => setTutorCognitoId(e.detail.value!)}
                  required
                />
              </IonItem>
              <IonButton expand="block" type="submit" color="dark" className="ion-margin-top">
                Asignar Tutor
              </IonButton>
            </form>
          </IonCardContent>
        </IonCard>

        {/* Sección: Consultar Historial Completo */}
        <IonCard>
          <IonCardHeader>
            <IonCardTitle>Consultar Tesis</IonCardTitle>
          </IonCardHeader>
          <IonCardContent>
            <form onSubmit={handleSearchHistory}>
              <IonItem lines="full">
                <IonLabel position="stacked">ID de Tesis a Buscar</IonLabel>
                <IonInput
                  type="number"
                  placeholder="Ingrese el ID a consultar"
                  value={searchThesisId}
                  onIonChange={(e) => setSearchThesisId(e.detail.value!)}
                  required
                />
              </IonItem>
              <IonButton expand="block" type="submit" fill="outline" color="dark" className="ion-margin-top">
                Buscar Historial
              </IonButton>
            </form>
          </IonCardContent>
        </IonCard>

        {thesisHistory && (
          <IonCard color="light">
            <IonCardHeader>
              <IonCardTitle>[#{thesisHistory.thesisId}] {thesisHistory.title}</IonCardTitle>
            </IonCardHeader>
            <IonCardContent>
              <p><strong>Estudiante:</strong> {thesisHistory.studentCognitoId}</p>
              <p><strong>Tutor Asignado:</strong> {thesisHistory.tutorCognitoId || 'Sin asignar'}</p>
              <p>
                <strong>Estado:</strong>{' '}
                <IonBadge color={thesisHistory.status === 'APPROVED' ? 'success' : 'warning'}>
                  {thesisHistory.status}
                </IonBadge>
              </p>
              <p><strong>Entregas realizadas:</strong> {thesisHistory.submissions?.length || 0}</p>

              <IonButton
                size="small"
                fill="clear"
                color="medium"
                onClick={() => setThesisHistory(null)}
                className="ion-margin-top"
              >
                Cerrar consulta
              </IonButton>
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