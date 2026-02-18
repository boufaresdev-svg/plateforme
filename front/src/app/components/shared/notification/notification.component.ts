import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, Notification } from '../../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-container">
      <div *ngFor="let notification of notifications" 
           class="notification" 
           [class.success]="notification.type === 'success'"
           [class.error]="notification.type === 'error'"
           [class.warning]="notification.type === 'warning'"
           [class.info]="notification.type === 'info'"
           [@slideIn]>
        <div class="notification-content">
          <span class="notification-icon">
            <span *ngIf="notification.type === 'success'">✓</span>
            <span *ngIf="notification.type === 'error'">✕</span>
            <span *ngIf="notification.type === 'warning'">⚠</span>
            <span *ngIf="notification.type === 'info'">ℹ</span>
          </span>
          <span class="notification-message">{{ notification.message }}</span>
          <button class="notification-close" (click)="close(notification.id)">×</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 80px;
      right: 20px;
      z-index: 10000;
      display: flex;
      flex-direction: column;
      gap: 10px;
      max-width: 400px;
    }

    .notification {
      padding: 16px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      animation: slideIn 0.3s ease-out;
      background: white;
      border-left: 4px solid;
    }

    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .notification.success {
      border-left-color: #10b981;
      background: #f0fdf4;
    }

    .notification.error {
      border-left-color: #ef4444;
      background: #fef2f2;
    }

    .notification.warning {
      border-left-color: #f59e0b;
      background: #fffbeb;
    }

    .notification.info {
      border-left-color: #3b82f6;
      background: #eff6ff;
    }

    .notification-content {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .notification-icon {
      font-size: 20px;
      font-weight: bold;
      min-width: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .notification.success .notification-icon {
      color: #10b981;
    }

    .notification.error .notification-icon {
      color: #ef4444;
    }

    .notification.warning .notification-icon {
      color: #f59e0b;
    }

    .notification.info .notification-icon {
      color: #3b82f6;
    }

    .notification-message {
      flex: 1;
      color: #1f2937;
      font-size: 14px;
      line-height: 1.5;
    }

    .notification-close {
      background: none;
      border: none;
      font-size: 24px;
      color: #6b7280;
      cursor: pointer;
      padding: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.2s;
    }

    .notification-close:hover {
      color: #1f2937;
    }
  `]
})
export class NotificationComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.notifications$.subscribe(notifications => {
      this.notifications = notifications;
    });
  }

  close(id: string) {
    this.notificationService.remove(id);
  }
}
