# Data Model

## Trip

A journey containing all travel information.

Fields:

- id
- name
- startDate
- endDate
- homeCurrency
- createdDate


## Expense

A record of money spent during a trip.

Fields:

- id
- tripId
- date
- description
- category
- foreignAmount
- foreignCurrency
- exchangeRate
- homeAmount
- homeCurrency
- notes
- createdDate


## Booking

Future entity for accommodation and transport.

Fields:

- id
- tripId
- type
- title
- location
- startDate
- endDate
- cost
- currency
- referenceNumber
- notes


## Place

Future entity for places to visit.

Fields:

- id
- tripId
- name
- location
- category
- notes
- visited