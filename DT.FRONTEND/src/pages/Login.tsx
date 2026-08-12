import React, { useState } from 'react';
import {
  IonContent,
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonItem,
  IonLabel,
  IonInput,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonToast,
  IonSpinner,
} from '@ionic/react';
import { useHistory } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  'cognito:groups'?: string[];
  [key: string]: any;
}

// Configuración obtenida de tu User Pool y App Client de AWS Cognito
const COGNITO_CLIENT_ID = "646le4a8cmshl4c5mdgnkd8ih8";
const REGION = "us-east-1";

export const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  
  const { login } = useAuth();
  const history = useHistory();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!username || !password) {
      setToastMessage('Por favor ingresa tu usuario y contraseña');
      setShowToast(true);
      return;
    }

    setLoading(true);

    try {
      // Petición directa a la API de AWS Cognito (USER_PASSWORD_AUTH)
      const response = await fetch(`https://cognito-idp.${REGION}.amazonaws.com/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-amz-json-1.1',
          'X-Amz-Target': 'AWSCognitoIdentityProviderService.InitiateAuth',
        },
        body: JSON.stringify({
          AuthFlow: 'USER_PASSWORD_AUTH',
          ClientId: COGNITO_CLIENT_ID,
          AuthParameters: {
            USERNAME: username,
            PASSWORD: password,
          },
        }),
      });

      const data = await response.json();

      if (!response.ok || data.__type) {
        throw new Error(data.message || 'Credenciales incorrectas');
      }

      // Token JWT devuelto por Cognito
      const idToken = data.AuthenticationResult.IdToken;

      // Guardar token en el AuthContext (AuthContext se encarga de procesar y crear el perfil)
      await login(idToken);

      // Decodificar el token únicamente para saber a qué ruta redirigir
      const decoded: DecodedToken = jwtDecode(idToken);
      const groups = decoded['cognito:groups'] || [];

      // Redirigir según el rol
      if (groups.includes('ADMIN')) {
        history.push('/admin');
      } else if (groups.includes('TUTOR')) {
        history.push('/tutor');
      } else if (groups.includes('STUDENT')) {
        history.push('/student');
      } else {
        history.push('/unauthorized');
      }
    } catch (err: any) {
      setToastMessage(err.message || 'Error al iniciar sesión. Revisa tus credenciales.');
      setShowToast(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="primary">
          <IonTitle>DocuTesis - Autenticación</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <IonCard>
          <IonCardHeader>
            <IonCardTitle>Acceso al Sistema</IonCardTitle>
          </IonCardHeader>
          <IonCardContent>
            <form onSubmit={handleLogin}>
              <IonItem lines="full">
                <IonLabel position="stacked">Usuario / Correo</IonLabel>
                <IonInput
                  placeholder="Ej: usuario@ejemplo.com"
                  value={username}
                  onIonChange={(e) => setUsername(e.detail.value!)}
                  type="text"
                  required
                />
              </IonItem>

              <IonItem lines="full" className="ion-margin-top">
                <IonLabel position="stacked">Contraseña</IonLabel>
                <IonInput
                  placeholder="Ingrese su contraseña"
                  value={password}
                  onIonChange={(e) => setPassword(e.detail.value!)}
                  type="password"
                  required
                />
              </IonItem>

              <IonButton 
                expand="block" 
                type="submit" 
                className="ion-margin-top"
                disabled={loading}
              >
                {loading ? <IonSpinner name="crescent" /> : 'Ingresar'}
              </IonButton>
            </form>
          </IonCardContent>
        </IonCard>

        <IonToast
          isOpen={showToast}
          onDidDismiss={() => setShowToast(false)}
          message={toastMessage}
          duration={3000}
          color="danger"
        />
      </IonContent>
    </IonPage>
  );
};