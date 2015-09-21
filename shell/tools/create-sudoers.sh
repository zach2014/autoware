#!/bin/bash
echo "Must run the script against with root user"
[ $1 ] || { 
    echo "Usage: $0 <UserName>" 
    exit 1 
} 
NEW_USER=$1
[[ -z "$NEW_USER" ]] && echo "NEW_USER is not set. Exiting."

if ! getent group $NEW_USER>/dev/null; then
    echo "Creating a group called $NEW_USER"
    groupadd $NEW_USER
fi

if ! getent passwd $NEW_USER >/dev/null; then
    echo "Creating a user called $NEW_USER"
    useradd -g $NEW_USER -s /bin/bash -d /home/$NEW_USER -m $NEW_USER
fi

echo "Giving new user passwordless sudo privileges"
# UEC images ``/etc/sudoers`` does not have a ``#includedir``, add one
grep -q "^#includedir.*/etc/sudoers.d" /etc/sudoers ||
    echo "#includedir /etc/sudoers.d" >> /etc/sudoers
( umask 226 && echo "$NEW_USER ALL=(ALL) NOPASSWD:ALL" \
    > /etc/sudoers.d/50_${NEW_USER}_sh )
echo "New user: {$NEW_USER}/{$NEW_USER}"
