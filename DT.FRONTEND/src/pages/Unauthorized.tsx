import React from 'react';
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
} from '@ionic/react';
import { useHistory } from 'react-router-dom';

export const Unauthorized: React.FC = () => {
  const history = useHistory();

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="danger">
          <IonTitle>Acceso Denegado</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding ion-text-center">
        <IonCard color="light">
          <IonCardHeader>
            <IonCardTitle>403 - Sin Autorización</IonCardTitle>
          </IonCardHeader>
          <IonCardContent>
            <p>No tienes los permisos requeridos para acceder a esta sección.</p>
            <IonButton expand="block" color="primary" className="ion-margin-top" onClick={() => history.push('/login')}>
              Volver al Login
            </IonButton>
          </IonCardContent>
        </IonCard>
      </IonContent>
    </IonPage>
  );
};