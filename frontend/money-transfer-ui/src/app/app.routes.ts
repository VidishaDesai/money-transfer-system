import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Register } from './auth/register/register';
import { Login } from './auth/login/login';
import { Transactions } from './user/transactions/transactions';
import { Transfer } from './user/transfer/transfer';
import { Rewards } from './user/rewards/rewards';
import { UserDashboard } from './user/user-dashboard/user-dashboard';
import { authGuard } from './core/guards/auth.guard';
import { Approvals } from './admin/approvals/approvals';
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';
import { Deposit } from './admin/deposit/deposit';
import { AdminTransactions } from './admin/admin-transactions/admin-transactions';
import { Accounts } from './admin/accounts/accounts';
import { Analytics } from './admin/analytics/analytics';
import { Rollbacks } from './admin/rollbacks/rollbacks';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'register', component: Register },
  { path: 'login', component: Login },

  {
    path: 'dashboard',
    component: UserDashboard,
    canActivate: [authGuard],
    children: [
      { path: 'transfer', component: Transfer },
      { path: 'transactions', component: Transactions },
      { path: 'rewards', component: Rewards }
    ]
  },

  {
    path: 'admin',
    component: AdminDashboard,
    children: [
      // { path: '', redirectTo: 'approvals', pathMatch: 'full' },
      { path: 'approvals', component: Approvals },
      { path: 'deposit', component: Deposit },
      { path: 'rollbacks', component: Rollbacks },
      { path: 'accounts', component: Accounts },
      { path: 'transactions', component: AdminTransactions },
      { path: 'analytics', component: Analytics }
    ]
  },
  { path: '**', redirectTo: '' }
];