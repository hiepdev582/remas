<script setup lang="ts">
import type { BaseStepItem } from "~/components/base/Steps.vue";
import { useNotificationService } from "~/features/notification/service";
import type { Notification } from "~/features/notification/types";
import { NotificationType } from "~/features/notification/types";

const authStore = useAuthStore();
const authService = useAuthService();

const isOpenProcessModal = ref(false);

const processSteps: BaseStepItem[] = [
  { title: "1. Booking Confirmation (Call Cust.)", status: StepStatus.FINISH },
  { title: "2. Document Verification (Valid Only)", status: StepStatus.FINISH },
  { title: "3. Pre-trip Prep", status: StepStatus.FINISH },
  {
    title: "4. Identity & Handover",
    status: StepStatus.FINISH,
  },
  { title: "5. Mid-trip Follow-up", status: StepStatus.FINISH },
  {
    title: "6. Vehicle Return & Surcharges",
    status: StepStatus.FINISH,
  },
  {
    title: "7. Post-Trip & Retention",
    status: StepStatus.FINISH,
  },
];

const notificationService = useNotificationService();
const notifications = ref<Notification[]>([]);
const unreadCount = ref(0);
let sseSource: EventSource | null = null;

const fetchNotifications = async () => {
  try {
    const res: any = await notificationService.getAll();
    notifications.value = res;
    const countRes: any = await notificationService.getUnreadCount();
    unreadCount.value = countRes;
  } catch (error) {
    console.error("Failed to load notifications", error);
  }
};

const handleMarkAsRead = async (item: Notification) => {
  if (item.isRead) {
    navigate(item);
    return;
  }
  try {
    await notificationService.markAsRead(item.id);
    item.isRead = true;
    if (unreadCount.value > 0) {
      unreadCount.value--;
    }
    navigate(item);
  } catch (error) {
    console.error("Failed to mark notification as read", error);
  }
};

const handleMarkAllAsRead = async () => {
  try {
    await notificationService.markAllAsRead();
    notifications.value.forEach((n) => (n.isRead = true));
    unreadCount.value = 0;
  } catch (error) {
    console.error("Failed to mark all as read", error);
  }
};

const navigate = (item: Notification) => {
  if (item.type === NotificationType.INACTIVE_CUSTOMER) {
    navigateTo(ROUTES.CUSTOMER);
  } else {
    navigateTo(ROUTES.CONTRACT);
  }
};

const formatRelativeTime = (dateStr: string) => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  if (diffMins < 1) return "Just now";
  if (diffMins < 60) return `${diffMins}m ago`;
  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `${diffHours}h ago`;
  const diffDays = Math.floor(diffHours / 24);
  return `${diffDays}d ago`;
};

const setupSse = () => {
  if (!authStore.token) return;
  const config = useRuntimeConfig();
  const url = `${config.public.apiBase}/notification/stream?token=${authStore.token}`;

  sseSource = new EventSource(url);

  sseSource.addEventListener("notification", (event: any) => {
    const newNotif: Notification = JSON.parse(event.data);
    notifications.value.unshift(newNotif);
    toast.info(`New Notification: ${newNotif.title}`);
  });

  sseSource.addEventListener("unread_count", (event: any) => {
    unreadCount.value = Number(event.data);
  });

  sseSource.onerror = (err) => {
    console.error("SSE connection error, attempting reconnect...", err);
  };
};

const handleClickLogout = async () => {
  try {
    const responseMessage = await authService.logout();
    toast.success(responseMessage || "Logged out successfully!");
    authStore.clearAuth();
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Logout failed!";
    toast.error(apiMessage);
  }
};

onMounted(async () => {
  if (authStore.isLoggedIn) {
    await fetchNotifications();
    setupSse();
  }
});

onUnmounted(() => {
  if (sseSource) {
    sseSource.close();
  }
});
</script>

<template>
  <a-layout-header
    :style="{
      height: '48px',
      lineHeight: '48px',
      paddingInline: '24px',
      color: 'var(--color-text-primary)',
      backgroundColor: '#ffffff',
      borderBottom: '1px solid #f3f4f6',
      userSelect: 'none',
    }"
  >
    <div class="h-full flex items-center justify-between">
      <section></section>
      <section class="h-full flex items-center justify-center gap-3">
        <!-- Flow Icon Button -->
        <BasePopover :placement="PopoverPlacement.TOP_LEFT">
          <template #content>
            <div class="w-48 flex flex-col">
              <p class="text-xs font-semibold text-slate-400 mb-3">
                Select Process Flow
              </p>
              <div
                class="cursor-pointer flex items-center gap-2.5 group rounded select-none hover:bg-primary/5 hover:text-primary transition-all duration-150"
                @click="isOpenProcessModal = true"
              >
                <div
                  class="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center text-slate-500 group-hover:bg-primary/10 group-hover:text-primary transition-all duration-150"
                >
                  <Icon name="mdi:car" size="18" />
                </div>
                <div class="flex flex-col">
                  <p
                    class="mb-0 text-xs font-semibold leading-tight text-slate-700 group-hover:text-primary"
                  >
                    Car Rental
                  </p>
                  <p class="mb-0 text-[10px] text-slate-400 mt-0.5">
                    Item handover process
                  </p>
                </div>
              </div>
            </div>
          </template>
          <div
            class="cursor-pointer flex items-center justify-center w-8 h-8 rounded-lg hover:bg-slate-50 border border-transparent hover:border-slate-100 text-slate-500 hover:text-primary transition-all duration-150"
          >
            <Icon name="fluent:flow-dot-16-regular" size="20" />
          </div>
        </BasePopover>

        <!-- Notification Popover -->
        <BasePopover :placement="PopoverPlacement.TOP_LEFT">
          <template #content>
            <div class="w-80 flex flex-col max-h-96">
              <div
                class="flex items-center justify-between pb-2 border-b border-slate-100 mb-2 px-1"
              >
                <span class="text-xs font-bold text-slate-800"
                  >Notifications</span
                >
                <span
                  v-if="unreadCount > 0"
                  class="text-[10px] text-primary font-semibold hover:underline cursor-pointer"
                  @click="handleMarkAllAsRead"
                >
                  Mark all as read
                </span>
              </div>

              <div class="overflow-y-auto flex-grow max-h-72">
                <div
                  v-if="notifications.length === 0"
                  class="py-6 flex justify-center items-center"
                >
                  <BaseEmpty description="No notifications" />
                </div>
                <div v-else class="flex flex-col gap-1">
                  <div
                    v-for="item in notifications"
                    :key="item.id"
                    class="p-2.5 rounded-lg hover:bg-slate-50 cursor-pointer flex gap-3 transition-colors relative"
                    :class="{
                      'bg-primary/5 hover:bg-primary/10': !item.isRead,
                    }"
                    @click="handleMarkAsRead(item)"
                  >
                    <div
                      class="w-8 h-8 rounded-full flex items-center justify-center shrink-0"
                      :class="{
                        'bg-orange-100 text-orange-600':
                          item.type === NotificationType.UPCOMING_HANDOVER,
                        'bg-indigo-100 text-indigo-600':
                          item.type === NotificationType.UPCOMING_RETURN,
                        'bg-rose-100 text-rose-600':
                          item.type === NotificationType.INACTIVE_CUSTOMER,
                      }"
                    >
                      <Icon
                        :name="
                          item.type === NotificationType.UPCOMING_HANDOVER
                            ? 'mdi:car-key'
                            : item.type === NotificationType.UPCOMING_RETURN
                              ? 'mdi:car-back'
                              : 'mdi:account-alert-outline'
                        "
                        size="16"
                      />
                    </div>
                    <div class="flex flex-col min-w-0 pr-2">
                      <p
                        class="text-xs font-bold text-slate-800 mb-0.5 leading-snug"
                      >
                        {{ item.title }}
                      </p>
                      <p
                        class="text-[10px] text-slate-500 mb-0.5 leading-relaxed"
                      >
                        {{ item.content }}
                      </p>
                      <p class="text-[9px] text-slate-400 mb-0">
                        {{ formatRelativeTime(item.createdAt) }}
                      </p>
                    </div>
                    <!-- Unread dot -->
                    <span
                      v-if="!item.isRead"
                      class="absolute top-3 right-3 w-1.5 h-1.5 bg-primary rounded-full"
                    ></span>
                  </div>
                </div>
              </div>
            </div>
          </template>
          <div
            class="cursor-pointer flex items-center justify-center w-8 h-8 rounded-lg hover:bg-slate-50 border border-transparent hover:border-slate-100 text-slate-500 hover:text-primary transition-all duration-150"
            @click="fetchNotifications"
          >
            <BaseBadge
              :count="unreadCount"
              :showZero="false"
              :overflowCount="99"
            >
              <Icon name="fluent:alert-16-regular" size="20" />
            </BaseBadge>
          </div>
        </BasePopover>

        <!-- User Avatar Popover -->
        <BasePopover :placement="PopoverPlacement.TOP_LEFT">
          <template #content>
            <div class="w-44 flex flex-col">
              <!-- User Info Header -->
              <div class="flex items-center gap-2.5 pb-2 px-1">
                <BaseAvatar class="bg-primary flex-shrink-0" :size="32">
                  {{ authStore.adminInfo?.username?.[0]?.toUpperCase() }}
                </BaseAvatar>
                <div class="flex flex-col min-w-0">
                  <p
                    class="font-semibold text-[13px] text-slate-800 truncate mb-0 leading-tight"
                  >
                    {{ authStore.adminInfo?.username }}
                  </p>
                  <p class="text-[11px] text-slate-400 truncate mb-0 mt-0.5">
                    {{ authStore.adminInfo?.email }}
                  </p>
                </div>
              </div>

              <!-- Divider -->
              <div class="border-t border-slate-100 -mx-3 my-1"></div>

              <!-- Action Items -->
              <div
                class="cursor-pointer flex items-center gap-2 px-1 py-1.5 group rounded select-none hover:text-[var(--color-error)] hover:bg-red-50/70 transition-all duration-150"
                @click="handleClickLogout"
              >
                <Icon
                  name="material-symbols:logout-rounded"
                  class="group-hover:text-[var(--color-error)]"
                  :size="16"
                />
                <p class="mb-[1px] text-xs font-medium">Logout</p>
              </div>
            </div>
          </template>
          <BaseAvatar class="cursor-pointer bg-primary ml-2">
            {{ authStore.adminInfo?.username?.[0]?.toUpperCase() }}
          </BaseAvatar>
        </BasePopover>
      </section>
    </div>
  </a-layout-header>

  <!-- Process Flow Modal -->
  <a-modal
    v-model:open="isOpenProcessModal"
    title="Rental Item Handover Process Flow"
    width="520px"
    centered
    :footer="null"
  >
    <div class="py-4">
      <BaseSteps
        size="small"
        :current="5"
        :items="processSteps"
        :direction="StepDirection.VERTICAL"
      >
        <template #description-0>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>Name, Phone, Pickup Location</li>
            <li>Start/Return Dates</li>
            <li>Total Price & VETC Fee</li>
          </ul>
        </template>
        <template #description-1>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>Vehicle Inspection Certificate</li>
            <li>Vehicle Registration / Mortgage Doc</li>
            <li>Civil Liability & Physical Damage Insurance</li>
          </ul>
        </template>
        <template #description-2>
          <p class="text-xs text-slate-500 mt-1">
            Prepare: Contract & Handover Doc
          </p>
        </template>
        <template #description-3>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>
              ID/License: Chip-ID + VNeID (Lvl 2) | Physical/Electronic License
              (
              <a
                target="_blank"
                href="https://gplx.csgt.bocongan.gov.vn/"
                class="underline text-blue-500"
                >Check</a
              >
              )
            </li>
            <li>Photos: ID & License (both sides), Cust + Car</li>
            <li>
              Inspection: Photos/Videos (4 angles, interior, ODO, battery) =>
              Record details => Sign Contract & Handover report
            </li>
            <li>Amenities: Glovebox (Docs, Masks) | Footwell (Wipes)</li>
            <li>
              Hotlines: BHTNDS (1900 54 54 58) | BHTV (1900 1562) | VinFast
              (1900 232389)
            </li>
          </ul>
        </template>
        <template #description-4>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>Short-term: Contact 2-4h before return</li>
            <li>Monthly: Contact a few days before end</li>
          </ul>
        </template>
        <template #description-5>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>
              Check & Compare: Exterior, interior, rev-cam, charging port, ODO,
              battery vs. initial report
            </li>
            <li>
              Docs/Accessories: Check docs + items (wipes, masks, pillows,
              brush, dashcam, sensors, film, mats, holder) => Return cust. docs
              => Sign return form
            </li>
            <li>
              <span>Surcharges:</span>
              <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
                <li>Over-mileage (>300km/day or >3k km/mo): 3k VND/km</li>
                <li>Late return: 80k VND/h (>5h = 1 day)</li>
                <li>Dirty car: 100k VND (mud/dirt)</li>
                <li>Odor: 300k VND (smoke/smell)</li>
                <li>Battery: (Delivered % - Returned %) x 800 VND</li>
              </ul>
            </li>
          </ul>
        </template>
        <template #description-6>
          <ul class="text-xs text-slate-500 mt-1 list-disc pl-4">
            <li>
              Thank-you text: "Thank you for keeping the car clean. Hope to
              serve you again soon!"
            </li>
            <li>Promo: Discount for returning customers.</li>
          </ul>
        </template>
      </BaseSteps>
    </div>
  </a-modal>
</template>
