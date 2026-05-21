import { Routes } from '@angular/router';
import { Register } from './register/register';
import { Login } from './login/login';
import { authGuard } from './auth-guard';
import { notAuthGuard } from './not-auth-guard';
import { MainLayout } from './layout/main-layout/main-layout';
import { Events } from './events/events';
import { EventDetail } from './event-detail/event-detail';
import { EventCreate } from './event-create/event-create';
import { MyEvents } from './my-events/my-events';
import { JoinedEvents } from './joined-events/joined-events';
import { ArchivedEvents } from './archived-events/archived-events';
import { EventEdit } from './event-edit/event-edit';
import { EventSearch } from './event-search/event-search';

export const routes: Routes = [
  { path: 'register', component: Register, canActivate: [notAuthGuard] },
  { path: 'login', component: Login, canActivate: [notAuthGuard] },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'events' },
      { path: 'events/create', component: EventCreate },
      { path: 'events/:id', component: EventDetail },
      { path: 'events', component: Events },
      { path: 'my-events', component: MyEvents },
      { path: 'joined-events', component: JoinedEvents },
      { path: 'archived-events', component: ArchivedEvents },
      { path: 'events/edit/:id', component: EventEdit },
      { path: 'search', component: EventSearch },
    ],
  },
  { path: '**', redirectTo: 'events' },
];
