import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export const ComponentRegistry = {
    button: {
        component: Button,
        defaultProps: {
            children: 'Click me',
            variant: 'default'
        },
        displayName: 'Button'
    },

    input: {
        component: Input,
        defaultProps: {
            placeholder: 'Enter text here...',
            type: 'text'
        },
        displayName: 'Input'
    },

    card: {
        component: Card,
        defaultProps: {
            children: 'Card content',
        },
        displayName: 'Card'
    },

    label: {
        component: Label,
        defaultProps:{
            children: 'Label',
        },
        displayName: 'Label'
    }
};