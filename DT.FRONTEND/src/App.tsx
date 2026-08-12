import React from 'react';
import { Redirect, Route } from 'react-router-dom';
import { IonApp, IonRouterOutlet, setupIonicReact } from '@ionic/react';
import { IonReactRouter } from '@ionic/react-router';

/* Estilos CSS obligatorios de Ionic */
import '@ionic/react/css/core.css';
import '@ionic/react/css/normalize.css';
import '@ionic/react/css/structure.css';
import '@ionic/react/css/typography.css';
import '@ionic/react/css/padding.css';
import '@ionic/react/css/float-elements.css';
import '@ionic/react/css/text-alignment.css';
import '@ionic/react/css/text-transformation.css';
import '@ionic/react/css/flex-utils.css';
import '@ionic/react/css/display.css';

/* Contexto y Protección de Rutas */
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';

/* Páginas y Vistas */
import { Login } from './pages/Login';
import { StudentDashboard } from './pages/StudentDashboard';
import { TutorDashboard } from './pages/TutorDashboard';
import { AdminDashboard } from './pages/AdminDashboard';
import { Unauthorized } from './pages/Unauthorized';

setupIonicReact();

export const App: React.FC = () => (
  <IonApp>
    <AuthProvider>
      <IonReactRouter>
        <IonRouterOutlet>
          {/* Rutas Públicas */}
          <Route exact path="/login" component={Login} />
          <Route exact path="/unauthorized" component={Unauthorized} />

          {/* Rutas Protegidas por Rol */}
          <ProtectedRoute exact path="/student" allowedRoles={['STUDENT']}>
            <StudentDashboard />
          </ProtectedRoute>

          <ProtectedRoute exact path="/tutor" allowedRoles={['TUTOR']}>
            <TutorDashboard />
          </ProtectedRoute>

          <ProtectedRoute exact path="/admin" allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>

          {/* Redirección Por Defecto */}
          <Route exact path="/">
            <Redirect to="/login" />
          </Route>
        </IonRouterOutlet>
      </IonReactRouter>
    </AuthProvider>
  </IonApp>
);

export default App;