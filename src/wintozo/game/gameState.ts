// =====================================================
// Spidi Clicker v3.1 — Game State & Logic
// =====================================================

export interface Upgrade {
  id: string;
  name: string;
  description: string;
  cost: number;
  multiplier: number;
  isAutoClicker: boolean;
  icon: string;
  duration: number; // в миллисекундах (24 часа)
  expiresAt?: number; // timestamp когда истечёт
  active: boolean;
}

export interface DailyGift {
  day: number;
  reward: string;
  coins?: number;
  specialReward?: string;
  claimed: boolean;
}

export interface GameState {
  coins: number;
  totalCoins: number;
  totalClicks: number;
  clickPower: number;
  baseClickPower: number;
  autoClickPerSecond: number;
  baseAutoClick: number;
  upgrades: Upgrade[];
  dailyGifts: DailyGift[];
  lastDailyCheck: string;
  currentDay: number;
  medal100k: boolean;
  goldenSpidi: boolean;
  selectedWallpaper: string;
  selectedIconPack: string;
  selectedMusic: string;
  musicEnabled: boolean;
  musicVolume: number;
  completedOnboarding: boolean;
  deviceType: 'phone' | 'tablet' | 'pc';
  customWallpapers: string[];
  customMusic: { name: string; url: string }[];
  lastSaved: string;
}

export const WALLPAPERS = [
  '/wallpapers/Wallpeper_1.webp',
  '/wallpapers/wallpaper_2.webp',
  '/wallpapers/Wallpeper_3.webp',
  '/wallpapers/wallpaper_4.webp',
  '/wallpapers/wallpaper_5.webp',
  '/wallpapers/wallpaper_6.webp',
  '/wallpapers/wallpaper_7.webp',
  '/wallpapers/wallpaper_8.webp',
];

export const LOGO_URL = '/game/logo.jpg';

export const ICON_URLS = {
  game: '/game/game.webp',
  upgrades: '/game/upgrades.webp',
  gifts: '/game/Gifts.webp',
  settings: '/game/settings.webp',
  clickPower: '/game/Sila_clicka.webp',
  upgradesSection: '/game/upgrades.webp',
  autoClicker: '/game/autoclicker.webp',
  settingsSection: '/game/settings.webp',
  medal100k: '/game/Medal.webp',
  goldenSpidi: '/game/golden_spidi.png',
  coin: '/game/Coin.webp',
  clickBtn: '/game/Click_default.jpg',
  phone: '/OOBE/phone.webp',
  tablet: '/OOBE/tablet.webp',
  pc: '/OOBE/computer.webp',
};

export const MUSIC_TRACKS = [
  {
    id: 'track1',
    name: 'Неофициальная мелодия',
    url: '/music/unofficial.mp3',
  },
  {
    id: 'track2',
    name: 'Spidi Original',
    url: '/music/Spidi_official.mp3',
  },
];

const ONE_DAY_MS = 24 * 60 * 60 * 1000;

const DEFAULT_UPGRADES: Upgrade[] = [
  {
    id: 'mult_x2',
    name: 'Множитель x2',
    description: 'Удваивает силу клика на 24 часа',
    cost: 250,
    multiplier: 2,
    isAutoClicker: false,
    icon: '✕2',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x3',
    name: 'Множитель x3',
    description: 'Утраивает силу клика на 24 часа',
    cost: 450,
    multiplier: 3,
    isAutoClicker: false,
    icon: '✕3',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x4',
    name: 'Множитель x4',
    description: 'Учетверяет силу клика на 24 часа',
    cost: 760,
    multiplier: 4,
    isAutoClicker: false,
    icon: '✕4',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x5',
    name: 'Множитель x5',
    description: 'Увеличивает силу клика в 5 раз на 24 часа',
    cost: 1000,
    multiplier: 5,
    isAutoClicker: false,
    icon: '✕5',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x10',
    name: 'Множитель x10',
    description: 'Увеличивает силу клика в 10 раз на 24 часа',
    cost: 10500,
    multiplier: 10,
    isAutoClicker: false,
    icon: '✕10',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x100',
    name: 'Множитель x100',
    description: 'Увеличивает силу клика в 100 раз на 24 часа',
    cost: 11500,
    multiplier: 100,
    isAutoClicker: false,
    icon: '✕100',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'mult_x1000',
    name: 'Множитель x1000',
    description: 'Увеличивает силу клика в 1000 раз на 24 часа',
    cost: 13000,
    multiplier: 1000,
    isAutoClicker: false,
    icon: '✕1K',
    duration: ONE_DAY_MS,
    active: false,
  },
  {
    id: 'autoclicker',
    name: 'Автокликер',
    description: 'Автоматически кликает 1 раз в секунду на 24 часа',
    cost: 500,
    multiplier: 1,
    isAutoClicker: true,
    icon: '🤖',
    duration: ONE_DAY_MS,
    active: false,
  },
];

const DEFAULT_DAILY_GIFTS: DailyGift[] = [
  { day: 1, reward: '100 монет', coins: 100, claimed: false },
  { day: 2, reward: '1 000 монет', coins: 1000, claimed: false },
  { day: 3, reward: '5 000 монет', coins: 5000, claimed: false },
  { day: 4, reward: '10 000 монет', coins: 10000, claimed: false },
  { day: 5, reward: 'Золотой Спиди (+50 сила клика навсегда)', specialReward: 'golden_spidi', claimed: false },
];

export const DEFAULT_STATE: GameState = {
  coins: 0,
  totalCoins: 0,
  totalClicks: 0,
  clickPower: 1,
  baseClickPower: 1,
  autoClickPerSecond: 0,
  baseAutoClick: 0,
  upgrades: DEFAULT_UPGRADES,
  dailyGifts: DEFAULT_DAILY_GIFTS,
  lastDailyCheck: '',
  currentDay: 1,
  medal100k: false,
  goldenSpidi: false,
  selectedWallpaper: WALLPAPERS[0],
  selectedIconPack: 'new',
  selectedMusic: MUSIC_TRACKS[0].id,
  musicEnabled: true,
  musicVolume: 0.3,
  completedOnboarding: false,
  deviceType: 'pc',
  customWallpapers: [],
  customMusic: [],
  lastSaved: '',
};

const SAVE_KEY = 'spidi_clicker_v31_save';

export function loadState(): GameState {
  try {
    const saved = localStorage.getItem(SAVE_KEY);
    if (saved) {
      const parsed = JSON.parse(saved) as Partial<GameState>;
      return {
        ...DEFAULT_STATE,
        ...parsed,
        upgrades: parsed.upgrades && parsed.upgrades.length > 0
          ? parsed.upgrades
          : DEFAULT_UPGRADES,
        dailyGifts: parsed.dailyGifts && parsed.dailyGifts.length > 0
          ? parsed.dailyGifts
          : DEFAULT_DAILY_GIFTS,
      };
    }
  } catch (e) {
    console.warn('Failed to load state', e);
  }
  return { ...DEFAULT_STATE };
}

export function saveState(state: GameState): void {
  try {
    localStorage.setItem(SAVE_KEY, JSON.stringify({
      ...state,
      lastSaved: new Date().toISOString(),
    }));
  } catch (e) {
    console.warn('Failed to save state', e);
  }
}

export function resetState(): void {
  try {
    localStorage.removeItem(SAVE_KEY);
  } catch (e) {
    console.warn('Failed to reset state', e);
  }
}

export function recalculateStats(state: GameState): GameState {
  const now = Date.now();
  let clickMultiplier = 1;
  let autoClick = 0;

  // Обновляем статус апгрейдов и считаем множители
  const updatedUpgrades = state.upgrades.map(up => {
    if (up.active && up.expiresAt && now < up.expiresAt) {
      if (up.isAutoClicker) {
        autoClick += up.multiplier;
      } else {
        clickMultiplier *= up.multiplier;
      }
      return up;
    } else if (up.active && up.expiresAt && now >= up.expiresAt) {
      // Истек
      return { ...up, active: false, expiresAt: undefined };
    }
    return up;
  });

  const newClickPower = state.baseClickPower * clickMultiplier;
  const newAutoClick = state.baseAutoClick + autoClick;

  return {
    ...state,
    upgrades: updatedUpgrades,
    clickPower: newClickPower,
    autoClickPerSecond: newAutoClick,
  };
}

export function formatNumber(n: number): string {
  if (n >= 1_000_000_000) return (n / 1_000_000_000).toFixed(2) + 'B';
  if (n >= 1_000_000) return (n / 1_000_000).toFixed(2) + 'M';
  if (n >= 1_000) return (n / 1_000).toFixed(1) + 'K';
  return Math.floor(n).toString();
}

export function getTodayStr(): string {
  return new Date().toDateString();
}

export function formatTimeRemaining(expiresAt: number): string {
  const now = Date.now();
  const diff = expiresAt - now;
  if (diff <= 0) return 'Истёк';
  
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  
  if (hours > 0) {
    return `${hours}ч ${minutes}м`;
  }
  return `${minutes}м`;
}
