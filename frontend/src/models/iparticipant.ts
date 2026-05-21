// models/iparticipant.ts

export interface Participant {
  id: number;
  name: string;
  surname: string;
  email: string;
}

export interface ParticipantResponse {
  content: Participant[];
  totalPages: number;
}
