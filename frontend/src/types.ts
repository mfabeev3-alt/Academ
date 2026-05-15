export interface SubjectRequestDto {
  name: string;
}

export interface SubjectResponseDto {
  id: number;
  name: string;
}

export interface ProfessorRequestDto {
  name: string;
  contact?: string;
}

export interface ProfessorResponseDto {
  id: number;
  name: string;
  contact?: string;
}

export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY',
}

export interface LocalTime {
  hour: number;
  minute: number;
  second: number;
  nano: number;
}

export interface ScheduleRequestDto {
  subjectId: number;
  professorId: number;
  room: string;
  dayOfWeek: DayOfWeek;
  startTime: string; // HH:mm:ss
  endTime: string; // HH:mm:ss
  activeWeeks: number[];
}

export interface ScheduleResponseDto {
  id: number;
  subjectName: string;
  professorName: string;
  professorContact?: string;
  room: string;
  dayOfWeek: DayOfWeek;
  startTime: LocalTime;
  endTime: LocalTime;
  activeWeeks: number[];
  isMoved?: boolean;
  movedDate?: string;
}

export interface LoginRequest {
  data: Record<string, string>;
}

export interface AuthResponse {
  token: string;
}
